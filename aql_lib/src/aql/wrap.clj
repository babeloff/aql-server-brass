
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
   * does not include 'create view'
   * replaces whitespace characters with blanks."
  (when query
    (try
      (let [qs (.second (.toSQLViews (.unnest query) "" "" "ID" "char"))
            qm (.ens (.dst query))]
        (-> (reduce #(str %1 (.get qs %2) "  ") "" qm)
            (st/replace #"[\n\t\r]" " ")
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

(sr/declarepath IS-QUERY)
(sr/providepath IS-QUERY
        (sr/cond-path
         (sr/must "query") "query"
         (sr/must :query) :query))

(sr/declarepath IS-SCHEMA)
(sr/providepath IS-SCHEMA
        (sr/cond-path
         (sr/must "schema") "schema"
         (sr/must :schema) :schema))

(sr/declarepath IS-ERR)
(sr/providepath IS-ERR
        (sr/cond-path
         (sr/must "err") "err"
         (sr/must :err) :err))

(defn xform-result
  "the tweeker is an optional transducer that gets
  applied to the result immediately before being
  placed into the vector. "
  [reqs tweeker gen]
  (log/debug "extract-result" reqs)
  (let [env-map (env->maps (sr/select-one [:env] gen))
        query-fn (fn [name] (query->sql (get (::query env-map) name)))
        schema-fn (fn [name] (schema->sql (get (::schema env-map) name)))]
    {:query
     (into {}
           (comp
            (map #(vector % (query-fn %)))
            tweeker)
           (sr/select-one [IS-QUERY] reqs))
     :schema
     (into []
           (map #(vector % (schema-fn %)))
           (sr/select-one [IS-SCHEMA] reqs))
     :error
     (into []
           (map #(.getMessage %))
           (sr/select-one [IS-ERR] gen))}))

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