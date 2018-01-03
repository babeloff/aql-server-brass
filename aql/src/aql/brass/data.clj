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
    {:name "sc0"
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
 
(def mapping-sx->s0 
    "A mapping between schema"
    {:name "m_sx_s0"
        :type :mapping 
        :objects ["sx" "s0"]
        :entities 
        #{  [["cot_event" "cot_event"] ["cot_event"]]
            [["cot_event" "cot_event_detail"] ["cot_event"]]
            [["cot_event_position" "cot_event"] ["cot_event_position"]]
            [["cot_event_position" "cot_event_detail"] ["cot_event_position"]]}
        :references 
        {   [["cot_event" "cot_event"] "cot_event_position_idx"
             ["cot_event_position"]]
            [["cot_event" "cot_event"] "cot_event_id" 
             ["cot_event"] "cot_event_id"]
            [["cot_event" "cot_event"] "cot_event_idx" 
             ["cot_event"]]}
        :attributes 
        {   "how" "how"
            "servertime" "servertime"
            "point_ce" "point_ce"
            "point_le" "point_le"
            "tileX" "tileX"
            "latitude" "latitude"
            "longitude" "longitude"
            "latitude" "latitude"
            "detail" "detail"
            "cot_type" "cot_type"
            "tileY" "tileY"
            "point_hae" "point_hae"}})
            

(def scx 
    {   :name "sx"
        :type :schema
        :extend "sql"
        :entities 
        #{  ["cot_event" "cot_event"] 
            ["cot_event" "cot_event_detail"] 
            ["cot_event_position" "cot_event"] 
            ["cot_event_position" "cot_event_detail"]}
        :attributes 
        {   "how"  [["cot_event" "cot_event"] "Varchar"]
            "servertime"  [["cot_event" "cot_event"] "Integer"]
            "point_ce" [["cot_event_position" "cot_event"] "Integer"]
            "point_le" [["cot_event_position" "cot_event"] "Integer"]
            "tileX" [["cot_event_position" "cot_event"] "Integer"]
            "latitude" [["cot_event_position" "cot_event"] "Real"]
            "longitude" [["cot_event_position" "cot_event"] "Real"]
            "detail"  [["cot_event" "cot_event_detail"] "Text"]
            "cot_type"  [["cot_event" "cot_event_detail"] "Varchar"]
            "tileY" [["cot_event_position" "cot_event_detail"] "Integer"]
            "point_hae" [["cot_event_position" "cot_event_detail"] "Integer"]}
        :references 
        {   "cot_event_position_idx" 
            [   ["cot_event_position" "cot_event_detail"] 
                ["cot_event_position" "cot_event"]]
            "cot_event_id" 
            [   ["cot_event_position" "cot_event"] 
                ["cot_event" "cot_event_detail"]]
            "cot_event_idx" 
            [   ["cot_event" "cot_event_detail"] 
                ["cot_event" "cot_event"]]}})
             
(def sc1 
    {:name "s1"
     :type :schema
     :extend "sql"
     :entities 
     #{"cot_event" "cot_event_detail"}
     :attributes 
     {"how"  ["cot_event" "Varchar"]
      "servertime"  ["cot_event" "Integer"]
      "point_ce" ["cot_event" "Integer"]
      "point_le" ["cot_event" "Integer"]
      "tileX" ["cot_event" "Integer"]
      "latitude" ["cot_event" "Real"]
      "longitude" ["cot_event" "Real"]
      "detail"  ["cot_event_detail" "Text"]
      "cot_type"  ["cot_event_detail" "Varchar"]
      "tileY" ["cot_event_detail" "Integer"]
      "point_hae" ["cot_event_detail" "Integer"]}
     :references 
     {  "cot_event_id" ["cot_event_detail" "cot_event"]}})


 

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"
;; 
;; Note that the original lost a reference [i.e. cot_event_id]
;;
(def schema-mapping
    {   :perturbation 
     {   :tables
      [ 
       {   :name "cot_event" 
           :columns 
           [   ["cot_event" "source_id"]
               ["cot_event" "how"]
               ["cot_event" "servertime"]
               ["cot_event_position" "point_ce"]
               ["cot_event_position" "point_le"]
               ["cot_event_position" "tileX"]
               ["cot_event_position" "longitude"]
               ["cot_event_position" "latitude"]]}
       {   :name "cot_event_detail"
           :columns
           [   ["cot_event_position" "point_hae"]
               ["cot_event" "detail"]
               ["cot_event_position" "tileY"]
               ["cot_event" "cot_type"]]}]}})

(def m0->1 
    {   :name "m01"
        :type mapping
        :schema ["s0" "s1"]
        :entities 
        [   ["cot_event" "cot_event"]
            ["cot_event_position" "cot_event_detail"]]   
        :references   
        [   [["cot_event" "source_id"] ["cot_event" "source_id"]]
            [["cot_event_position" "cot_event_id"] ["cot_event_detail" "cot_event_id"]]]
        :attributes
        [   [["cot_event" "how"] ["cot_event" "how"]]
            [["cot_event" "servertime"] ["cot_event" "servertime"]]

            [["cot_event" "detail"] ["cot_event_detail" "detail"]]
            [["cot_event" "cot_type"] ["cot_event_detail" "cot_type"]]

            [["cot_event_position" "point_ce"] ["cot_event" "point_ce"]]
            [["cot_event_position" "point_le"] ["cot_event" "point_le"]]
            [["cot_event_position" "tileX"] ["cot_event" "tileX"]]
            [["cot_event_position" "longitude"] ["cot_event" "longitude"]]
            [["cot_event_position" "latitude"] ["cot_event" "latitude"]]

            [["cot_event_position" "point_hae"] ["cot_event_detail" "point_hae"]]
            [["cot_event_position" "tileY"] ["cot_event_detail" "tileY"]]]})
    
(def m2-0->1 
    {   :name "m01"
        :type mapping
        :schema ["s0" "s1"]
        :entities 
        [   ["cot_event" "cot_event"]
            ["cot_event_position" "cot_event_detail"]]   
        :references   
        [   [["cot_event" "source_id"] ["cot_event" "source_id"]]
            [["cot_event_position" "cot_event_id"] ["cot_event_detail" "cot_event_id"]]]
        :attributes
        [   [["cot_event" "how"] ["cot_event" "how"]]
            [["cot_event" "servertime"] ["cot_event" "servertime"]]

            [["cot_event" "detail"] ["cot_event_detail" "detail"]]
            [["cot_event" "cot_type"] ["cot_event_detail" "cot_type"]]

            [["cot_event_position" "point_ce"] ["cot_event" "point_ce"]]
            [["cot_event_position" "point_le"] ["cot_event" "point_le"]]
            [["cot_event_position" "tileX"] ["cot_event" "tileX"]]
            [["cot_event_position" "longitude"] ["cot_event" "longitude"]]
            [["cot_event_position" "latitude"] ["cot_event" "latitude"]]

            [["cot_event_position" "point_hae"] ["cot_event_detail" "point_hae"]]
            [["cot_event_position" "tileY"] ["cot_event_detail" "tileY"]]]})
                        
        

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


