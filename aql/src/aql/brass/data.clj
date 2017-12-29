;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.brass.data
    (:require 
        (clojure [pprint :as pp]
                 [string :as st])))

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
         "cot_event_id" ["cot_event_position" "cot_event"]}})
         
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


