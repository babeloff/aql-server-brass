
(ns aql.wrap
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr]))
  (:import
   (catdata LineException)
   (catdata.aql
    AqlCmdLine)
   (catdata.aql.exp
    AqlEnv
    AqlParser
    AqlMultiDriver)))

(defn schema->sql [schema]
  (when schema
    (try
      (AqlCmdLine/schemaToSql schema)
      (catch Exception ex
        "cannot generate sql for schema"))))

(defn query->sql [query]
  "a slightly modified version of catdata.aql.AqlCmdLine/queryToSql
   notably the removal of create view"
  (when query
    (try
      (let [qs (.second (.toSQLViews (.unnest query) "" "" "ID" "char"))]
        (->> (.ens (.dst query))
             (reduce #(str %1 (.get qs %2) "\n\n") "")
             st/trim))
      (catch Exception ex
        (log/error "cannot generate sql for query" ex)))))


(defn env->maps [env]
  (let [env-defs (.-defs env)]
    {::instance (-> env-defs .-qs .-map)
     ::mapping (-> env-defs .-maps .-map)
     ::schema (-> env-defs .-schs .-map)
     ::transform (-> env-defs .-trans .-map)
     ::typeside (-> env-defs .-tys .-map)
     ::query (-> env-defs .-qs .-map)
     ::command (-> env-defs .-ps .-map)
     ::graph (-> env-defs .-gs .-map)
     ::comment (-> env-defs .-cs .-map)
     ::schema-colimit (-> env-defs .-scs .-map)
     ::constraint (-> env-defs .-eds .-map)}))

(defn xform-result [reqs gen]
  (log/debug "extract-result" reqs)
  (let [env-map (env->maps (sr/select-one [:env] gen))
        query-fn (partial get (::query env-map))
        schema-fn (partial get (::schema env-map))]
    {:query (->>
             (sr/select-one [:query] reqs)
             (map #(vector % (->> % query-fn query->sql)))
             (into []))
     :schema (->>
              (sr/select-one [:schema] reqs)
              (map #(vector % (schema->sql (schema-fn %))))
              (into []))
     :error (->>
             (sr/select-one [:err] gen)
             (map #(.getMessage %))
             (into []))}))

(defn private-field
  [field-name obj]
  (doto (-> obj (.getClass) (.getDeclaredField field-name))
    (.setAccessible true)
    (.get obj)))

(defn make-driver [model]
  (-> (AqlParser/getParser)
      (.parseProgram model)
      (AqlMultiDriver. (make-array String 1)  nil)))

(defn generate [model]
  (let [driver (make-driver model)]
    (.start driver)
    {:status (.toString driver)
     :env (.-env driver)
     :err (->> (.exn driver)
               (filter #(instance? LineException %))
               (into []))}))
