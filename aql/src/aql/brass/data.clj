;;
;; 

(ns aql.brass.data
    (:import (catdata.aql.exp 
                AqlEnv
                AqlParser 
                AqlMultiDriver)))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   database/server/baseline_schema_ddl.sql

(def sc0 "schema S = literal : sql {
    entities
        source 
        cot_event
        cot_event_position
    foreign_keys
        source_id : cot_event -> source
        cot_event_id : cot_event_position -> cot_event 
    path_equations 
        cot_event_position.cot_event_id = cot_event
    attributes
        name   : source -> Varchar
        channel : source -> Integer

        source_id : cot_event -> Varchar
        cot_type : cot_event -> Varchar
        how : cot_event -> Varchar
        detail : cot_event -> Text
        servertime : cot_event -> Integer

        cot_event_id : cot_event_position -> Integer
        point_hae : cot_event_position -> Integer
        point_ce : cot_event_position -> Integer
        point_le : cot_event_position -> Integer
        tileX : cot_event_position -> Integer
        tileY : cot_event_position -> Integer
        longitude : cot_event_position -> Real
        latitude : cot_event_position -> Real
 }")


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
  
