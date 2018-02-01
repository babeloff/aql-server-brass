
(ns aql.wrap
    (:require
        (clojure [pprint :as pp]
                 [string :as st])
        (com.rpl [specter :as sr]))
    (:import
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

(defn env->schema [env name]
    (-> env .defs .schs .map (get name)))

(defn env->schema-names [env] (-> env .defs .schs .map keys))

(defn env->schema->sql [aql-env name]
    (schema->sql (env->schema aql-env name)))

(defn query->sql [query]
    (when query
        (AqlCmdLine/queryToSql query)))

(defn env->query [env name]
    (-> env .defs .qs .map (get name)))

(defn env->query-names [env] (-> env .defs .qs .map keys))

(defn env->maps [env]
    (let [edefs (.defs env)]
        {:instance (-> edefs .qs .map)
         :mapping (-> edefs .maps .map)
         :schema (-> edefs .schs .map)
         :transform (-> edefs .trans .map)
         :typeside (-> edefs .tys .map)
         :query (-> edefs .qs .map)
         :command (-> edefs .ps .map)
         :graph (-> edefs .gs .map)
         :comment (-> edefs .cs .map)
         :schema-colimit (-> edefs .scs .map)
         :constraint (-> edefs .eds .map)}))

(defn env->query->sql [aql-env name]
    (query->sql (env->query aql-env name)))

(defn extract-sql-schema [aql-env req]
    (map #(vector % (env->schema->sql aql-env %)) req))

(defn extract-sql-query [aql-env req]
    (map #(vector % (env->query->sql aql-env %)) req))

(defn extract-sql [aql-env [key req]]
    (case key
        "query"
        [key (extract-sql-query aql-env req)]

        "schema"
        [key (extract-sql-schema aql-env req)]))

(defn extract-result [req-s aql-env]
    (into {} (map #(extract-sql aql-env %) req-s)))

(defn make-env [model]
    (let [parser (AqlParser/getParser)
          prog (.parseProgram parser model)
          drvr (AqlMultiDriver. prog (make-array String 1)  nil)]
        (.start drvr)
        (.env drvr)))
