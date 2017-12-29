;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.brass.data
    (:require 
        (clojure [pprint :as pp]
                 [string :as st]))
    (:import 
        (catdata.aql.exp 
            AqlEnv
            AqlParser 
            AqlMultiDriver)))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   database/server/baseline_schema_ddl.sql

(def sc0 
    {:name "S"
     :type :schema
     :extend "sql"
     :entities 
        #{"source" "cot_event" "cot_event_position"}
     :attributes 
        {"name" ["source" "Varchar"]
         "channel" ["source" "Varchar"]  
         "cot_type"  ["cot_event" "Varchar"] 
         "how"  ["cot_event" "Varchar"]
         "detail"  ["cot_event" "Text"]
         "servertime"  ["cot_event" "Integer"]
         "cot_event_id" ["cot_event_position" "Integer"]
         "point_hae" ["cot_event_position" "Integer"]
         "point_ce" ["cot_event_position" "Integer"]
         "point_le" ["cot_event_position" "Integer"]
         "tileX" ["cot_event_position" "Integer"]
         "tileY" ["cot_event_position" "Integer"]
         "latitude" ["cot_event_position" "Real"]
         "longitude" ["cot_event_position" "Real"]}
     :references 
        {"source_id" ["cot_event" "source"]
         "cot_event_id" ["cot_event_position" "cot_event"]}
     :path-equivs
        [[["cot_event_position" "cot_event_id"] ["cot_event"]]]})
 
(defn mapjoin [delimiter f col]
    (st/join delimiter (map f col)))


(defn wrap-schema [schema literal]
    (str "schema " (:name schema) 
        " = literal : " (:extend schema) 
        " {\n" literal "\n}\n"))
            
(defn serial-aql-schema [schema]
    (wrap-schema schema 
        (str 
            " entities " "\n"
            (st/join " " (:entities schema)) 
            "\n"
            " foreign_keys " "\n"
            (mapjoin " " 
                (fn [[key [src dst]]] 
                    (str key " : " src " -> " dst)) 
                (:references schema)) 
            "\n"                         
          ; " path_equations " "\n"
          ;  (mapjoin " " 
          ;       (fn [[left right]] 
          ;          (str (st/join "." left) " = " (st/join "." right)) 
          ;      (:path-equivs schema)
          ;  "\n"
            " attributes " "\n"
            (mapjoin " " 
                (fn [[key [src type]]] 
                    (str key " : " src " -> " type)) 
                (:attributes schema)))))
              


 ;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
 ;;  database/server/aql/src/aql/cp2_1_db.aql#L262

(def q01 "query q01 = literal : S -> S {
    entity
        result -> 
        {
            from ce:cot_event
            where ce.cot_type = 'a-n-A-C-F-m'
            attributes 
                cot_type -> ce.cot_type 
        }    
}")

(def q02 "query q02 = literal : S -> S {
    entity
        result -> 
        {
            from ce:cot_event
            where ce.servertime = 201705071635
            attributes 
                cot_type -> ce.cot_type
                how -> ce.how 
        }    
}")

;; It may be useful to pass in an array to update
;; see src/catdata/aql/gui/AqlCodeEditor:: makeEnv
(defn make-env 
    [prog] 
    (let [driver (AqlMultiDriver. prog (make-array String 1)  nil)]
        (.start driver)
        (let [last-env (.env driver)]
            (cond 
                (some? (.exn last-env)) last-env
                (not (.isEmpty (.keySet (.defs last-env)) )) last-env
                :else (throw (Exception. "my exception message"))))))
                

(defn permute 
    "register permutation" 
    [perm]
    (let [  initial (try 
                        (AqlParser/parseProgram perm)
                        ;;(catch LocException ex (.printStackTrace ex))
                        (catch Throwable ex (.printStackTrace ex)))
            start (System/currentTimeMillis)
            evn (make-env initial)
            middle (System/currentTimeMillis)]
        evn))
        

