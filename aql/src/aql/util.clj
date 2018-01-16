
(ns aql.util
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

(defn mapjoin [delimiter f col]
    (st/join delimiter (map f col)))

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
  
(defmulti serialize-name 
    (fn [name] 
        (cond 
            (string? name) ::string
            (vector? name) ::vector 
            :else ::default)))

(defmethod serialize-name ::string [name] name)
(defmethod serialize-name ::vector [name] (st/join "__" name))
(defmethod serialize-name ::default [name] "default")
    

(defn wrap-schema [schema literal]
    (str "schema " (:name schema) 
        " = literal : " (:extend schema) 
        " {\n" literal "\n}\n"))

(defn serialize-aql-schema [schema]
    (wrap-schema schema 
        (str 
            "\n"
            " entities " "\n   "
            (st/join "\n   " (map serialize-name (:entities schema))) 
            "\n"
            " foreign_keys " "\n   "
            (mapjoin "\n   " 
                (fn [[key [src dst]]] 
                    (str key " : " 
                        (serialize-name src) " -> " 
                        (serialize-name dst))) 
                (:references schema)) 
            "\n"                         
            " path_equations " "\n   "
            (mapjoin "\n   " 
                (fn [[left right]] 
                    (str (st/join "." left) " = " (st/join "." right))) 
                (:equations schema))
            "\n"
            " attributes " "\n   "
            (mapjoin "\n   " 
                (fn [[key [src type]]] 
                    (str key " : " 
                        (serialize-name src) " -> " type)) 
                (:attributes schema)))))

(defn norm-attr->ents [schema]
    (into #{} 
        (map 
            (fn [[key [ent type]]]
                (str ent "__" key))
            (:attributes schema))))
                                    
(defn norm-attr->attrs [schema]
    (into {} 
        (map 
            (fn [[key [ent type]]]
                (let [nent (str ent "__" key)]
                    [nent [nent type]]))
            (:attributes schema))))

(defn norm-refs->ents [schema])
(defn norm-attr->refs [schema])
(defn norm-refs->refs [schema])

(defn norm-aql-schema [schema]
    "Expand each attribute into its own entity.
    classes as entites are elimintated."
    wrap-schema schema
        (str
            " entities " 
            (norm-attr->ents schema)
            (norm-refs->ents schema)
            " foreign_keys "
            (norm-attr->refs schema)
            (norm-refs->refs schema)
            " attributes "
            (norm-attr->attrs schema)))
            
(defn make-env [model]
    (let [parser (AqlParser/getParser)
          prog (.parseProgram parser model)
          drvr (AqlMultiDriver. prog (make-array String 1)  nil)]
        (.start drvr)
        (.env drvr)))


(defn wrap-mapping [mapping literal]
    (str "mapping " (:name mapping) 
        " = literal : " (st/join " -> " (:schemas mapping)) 
        " {\n" literal "\n}\n"))

(defn serialize-aql-mapping-reference 
    "f -> N or f -> g"
    [[src-ent dest-ent] [src dest]]
    (cond 
        (nil? dest) 
        (str src " -> " dest-ent)

        :else 
        (str src " -> " dest))) 

(defn serialize-aql-mapping-attribute 
    "f -> N or f -> g"
    [[src-ent dest-ent] [src dest]]
    (str src " -> " dest))

(defn serialize-aql-mapping-entity 
    [[entity-key entity-value]]
    (let [  [src dest] (map serialize-name entity-key)
             fks (:references entity-value)
             attrs (:attributes entity-value)] 
        (str 
            " entity "
            (str src " -> " dest)
            "\n"
            " foreign_keys " "\n   "
            (mapjoin "\n   " #(serialize-aql-mapping-reference [src dest] %) fks)
            "\n"                         
            " attributes " "\n   "
            (mapjoin "\n   " #(serialize-aql-mapping-attribute [src dest] %) attrs))))        

(defn serialize-aql-mapping 
    [mapping] 
    (->> 
        (:entities mapping)
        (map serialize-aql-mapping-entity)      
        (st/join "\n")
        (wrap-mapping mapping)))
                 

