
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

;; It may be useful to pass in an array to update
;; see src/catdata/aql/gui/AqlCodeEditor:: makeEnv

(defn schema->sql [schema]
  (when schema
    (AqlCmdLine/schemaToSql schema)))

(defn query->sql [query]
  (when query
    (AqlCmdLine/queryToSql query)))

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

(defn env->schema->sql [env-map name]
  (schema->sql (get name (::schema env-map))))

(defn env->query->sql [env-map name]
  (query->sql (get name (::query env-map))))

(defn xform-result [reqs gen]
  (log/debug "extract-result" reqs)
  (let [env-map (env->maps (sr/select-one [:env] gen))]
    {:query (->>
             (sr/select-one [:query] reqs)
             (map #(vector % (env->query->sql env-map %)))
             (into []))
     :schema (->>
              (sr/select-one [:schema] reqs)
              (map #(vector % (env->schema->sql env-map %)))
              (into []))
     :error (->>
             (sr/select-one [:err] gen)
             (map #(.getMessage %))
             (into []))}))

(defn generate [model]
  (let [parser (AqlParser/getParser)
        prog (.parseProgram parser model)
        drvr (AqlMultiDriver. prog (make-array String 1)  nil)]
    (.start drvr)
    {:status (.toString drvr)
     :env (.-env drvr)
     :err (->> (.-exn drvr)
               (filter #(instance? LineException %))
               (into []))}))
