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
    {:name "s0"
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

(def sc1 
    {:name "s0"
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

(def sch->sch 
    "A mapping between schema"
    {:name "m1"
        :type :mapping 
        :objects ["s0" "sm01"]
        :entity 
        #{["ce1" "cot_event"]
          ["cot_event_position" "cot_event"]}})
 

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"
(def schema-mapping
    { "martiServerModel" 
        { "requirements"
            { "postgresqlPerturbation" 
                { "tables"
                    [ { "name" "cot_event" 
                        "columns" 
                            [   ["cot_event" "source_id"]
                                ["cot_event" "how"]
                                ["cot_event" "servertime"]
                                ["cot_event_position" "point_ce"]
                                ["cot_event_position" "point_le"]
                                ["cot_event_position" "tileX"]
                                ["cot_event_position" "longitude"]
                                ["cot_event_position" "latitude"]]}
                    
                      { "name" "cot_event_detail]"
                        "columns"
                              [   ["cot_event_position" "point_hae"]
                                  ["cot_event" "detail"]
                                  ["cot_event_position" "tileY"]
                                  ["cot_event" "cot_type"]]}]}}}})

;mapping m3 = literal : s3 -> s3 {
;    entity
;        Employee -> Employee
;    foreign_keys
;        manager -> manager.manager
;        worksIn -> worksIn
;    attributes
;        ename -> manager.ename
;        age -> manager.age
;        cummulative_age -> lambda e. (age(e) plus age(e))
;
;    entity
;        Department -> Department
;    foreign_keys	
;        secretary -> secretary
;    attributes
;        dname -> dname	
;        
;    options
;        dont_validate_unsafe = true // this isn't a real mapping		
;}

 ;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
 ;;  database/server/aql/src/aql/cp2_1_db.aql#L262

(def q01 "query q01 = literal : s0 -> s0 {
    entity
        result -> 
        {
            from ce:cot_event
            where ce.cot_type = 'a-n-A-C-F-m'
            attributes 
                cot_type -> ce.cot_type 
        }    
}")

(def q02 "query q02 = literal : s0 -> s0 {
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


