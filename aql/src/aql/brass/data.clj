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
        #{"source" "cot_event" "cot_position"}
     :attributes 
        {"name" ["source" "Varchar"]
         "channel" ["source" "Varchar"] 

         "cot_type"  ["cot_event" "Varchar"] 
         "how"  ["cot_event" "Varchar"]
         "detail"  ["cot_event" "Text"]
         "servertime"  ["cot_event" "Bigint"]

         "point_hae" ["cot_position" "Integer"]
         "point_ce" ["cot_position" "Integer"]
         "point_le" ["cot_position" "Integer"]
         "tileX" ["cot_position" "Integer"]
         "tileY" ["cot_position" "Integer"]
         "latitude" ["cot_position" "Real"]
         "longitude" ["cot_position" "Real"]}
     :references 
        {"has_source" ["cot_event" "source"]
         "has_cot_event" ["cot_position" "cot_event"]}})
 


;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"
;; 
;; Note that the original lost a reference [i.e. cot_event_id]
;; The schema-mapping is supplied and the... 
;;  scx, mapping-sx->s0, s1, and mapping-sx->s1 
;; ...should be derived from it.
;;
(def schema-mapping
    {   :perturbation 
     {   :tables
      [ 
       {   :name "cot_action" 
           :columns 
           [   ["cot_action" "source_id"]
               ["cot_action" "how"]
               ["cot_action" "servertime"]
               ["cot_position" "point_ce"]
               ["cot_position" "point_le"]
               ["cot_position" "tileX"]
               ["cot_position" "longitude"]
               ["cot_position" "latitude"]]}
       {   :name "cot_detail"
           :columns
           [   ["cot_position" "point_hae"]
               ["cot_action" "detail"]
               ["cot_position" "tileY"]
               ["cot_action" "cot_type"]]}]}})


(def scx 
    {   :name "sx"
        :type :schema
        :extend "sql"
        :entities 
        #{  ["source" "cot_action"] 
            ["cot_event" "cot_action"]
            ["cot_event" "cot_detail"] 
            ["cot_position" "cot_action"] 
            ["cot_position" "cot_detail"]}
        :attributes 
        {   "name" [["source" "cot_action"] "Varchar"]
            "channel" [["source" "cot_action"] "Varchar"] 
            
            "how"  [["cot_event" "cot_action"] "Varchar"]
            "servertime"  [["cot_event" "cot_action"] "Bigint"]

            "detail"  [["cot_event" "cot_detail"] "Text"]
            "cot_type"  [["cot_event" "cot_detail"] "Varchar"]

            "point_ce" [["cot_position" "cot_action"] "Integer"]
            "point_le" [["cot_position" "cot_action"] "Integer"]
            "tileX" [["cot_position" "cot_action"] "Integer"]
            "latitude" [["cot_position" "cot_action"] "Real"]
            "longitude" [["cot_position" "cot_action"] "Real"]

            "tileY" [["cot_position" "cot_detail"] "Integer"]
            "point_hae" [["cot_position" "cot_detail"] "Integer"]}
        :references 
        {   "has_source" 
            [   ["cot_event" "cot_action"]
                ["source" "cot_action"]]

            "cot_event_idx"
            [   ["cot_event" "cot_detail"] 
                ["cot_event" "cot_action"]]
            "cot_event_idy" 
            [   ["cot_event" "cot_action"] 
                ["cot_event" "cot_detail"]]
      
            "has_cot_event" 
            [   ["cot_position" "cot_action"] 
                ["cot_event" "cot_detail"]]
            
            "cot_position_idx"
            [   ["cot_position" "cot_detail"] 
                ["cot_position" "cot_action"]]
            "cot_position_idy" 
            [   ["cot_position" "cot_action"] 
                ["cot_position" "cot_detail"]]}})
            

(def mapping-sx->s0 
    "A mapping between schema
    "
    {   :name "m_sx_s0"
        :type :mapping 
        :schemas ["sx" "s0"]
        :entities
        {   [["source" "cot_action"] ["source"]] 
            {   :attributes 
                {   "name" "name" 
                    "channel" "channel"}}
            [["cot_event" "cot_action"] ["cot_event"]]
            {   :references 
                {   "has_source" "has_source"
                    "cot_event_idy" nil}
                :attributes
                {   "how" "how"
                    "servertime" "servertime"}}
            [["cot_event" "cot_detail"] ["cot_event"]]
            {   :references 
                {   "cot_event_idx" nil}
                :attributes 
                {   "detail" "detail"
                    "cot_type" "cot_type"}}
            [["cot_position" "cot_action"] ["cot_position"]]
            {   :references 
                {   "has_cot_event" "has_cot_event"
                    "cot_position_idy" nil}
                :attributes
                {   "point_ce" "point_ce"
                    "point_le" "point_le"
                    "tileX" "tileX"
                    "latitude" "latitude"
                    "longitude" "longitude"}}
            [["cot_position" "cot_detail"] ["cot_position"]]
            {   :references 
                {   "cot_position_idx" nil}
                :attributes
                {   "tileY" "tileY"
                    "point_hae" "point_hae"}}}})
                    

(def qx0 "query qx0 = toCoQuery m_sx_s0")
                                              
(def sc1 
    {:name "s1"
     :type :schema
     :extend "sql"
     :entities 
     #{"cot_action" "cot_detail"}
     :attributes 
     {  "name"  ["cot_action" "Varchar"]
        "channel"  ["cot_action" "Varchar"]
        "how"  ["cot_action" "Varchar"]
        "servertime"  ["cot_action" "Bigint"]
        "point_ce" ["cot_action" "Integer"]
        "point_le" ["cot_action" "Integer"]
        "tileX" ["cot_action" "Integer"]
        "latitude" ["cot_action" "Real"]
        "longitude" ["cot_action" "Real"]

        "detail"  ["cot_detail" "Text"]
        "cot_type"  ["cot_detail" "Varchar"]
        "tileY" ["cot_detail" "Integer"]
        "point_hae" ["cot_detail" "Integer"]}
     :references 
     {  "has_cot_action" ["cot_action" "cot_detail"]
        "cot_action_idx" ["cot_detail" "cot_action"]
        "cot_action_idy" ["cot_action" "cot_detail"]}})

(def mapping-sx->s1 
   "A mapping between schema"
   {    :name "m_sx_s1"
        :type :mapping 
        :schemas ["sx" "s1"]
        :entities
        {   [["source" "cot_action"] ["cot_action"]]
            {   :attributes
                {   "name" "name" 
                    "channel" "channel"}}
            [["cot_event" "cot_action"] ["cot_action"]]
            {   :references 
                {   "cot_event_idy" "cot_action_idy"
                    "has_source" nil}
                :attributes
                {   "how" "how"
                    "servertime" "servertime"}}
            [["cot_event" "cot_detail"] ["cot_detail"]]
            {   :references 
                {    "cot_event_idx" "cot_action_idx"}
                :attributes
                {   "detail" "detail"
                    "cot_type" "cot_type"}}
            [["cot_position" "cot_action"] ["cot_action"]]
            {   :references 
                {   "has_cot_event" "has_cot_action"
                    "cot_position_idy" "cot_action_idy"}
                :attributes
                {   "point_ce" "point_ce"
                    "point_le" "point_le"
                    "tileX" "tileX"
                    "latitude" "latitude"
                    "longitude" "longitude"}}
            [["cot_position" "cot_detail"] ["cot_detail"]]
            {   :references 
                {   "cot_position_idx" "cot_action_idx"}
                :attributes
                {"tileY" "tileY"
                    "point_hae" "point_hae"}}}})         
        
(def q1x "query q1x = toQuery m_sx_s1")

(def sql 
    "typeside sql = literal {
        imports sql
        java_functions 
            EqualStr : String, String -> Bool = \"return input[0].equals(input[1])\"
            EqualVc : Varchar, Varchar -> Bool = \"return input[0].equals(input[1])\"
            EqualInt : Integer, Integer -> Bool = \"return input[0].equals(input[1])\"
        }")
         

(def q1x0 "query q1x0 = [ q1x ; qx0 ]")

 ;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
 ;;  database/server/aql/src/aql/cp2_1_db.aql#L262

(def qs01 "query q01 = simple : s0  {
    from ce:cot_event
    where ce.cot_type = \"a-n-A-C-F-m\" 
    attributes 
        cot_type -> ce.cot_type 
        }")

(def qs01t "query q01t = [ q1x0 ; q01 ]")

(def qs01a "query q01a = simple : s0  {
    from ce:cot_event
    where EqualVc(ce.cot_type, \"a-n-A-C-F-m\") = true
    attributes 
        cot_type -> ce.cot_type 
        }")


(def qs02 
    "query q02 = simple : s0  {
    from ce:cot_event
    where ce.servertime = \"201705071635\" 
    attributes 
        cot_type -> ce.cot_type
        how -> ce.how 
    }")
    
(def qs02t "query q02t = [ q1x0 ; q02 ]")
    
    
