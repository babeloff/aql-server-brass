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
     :extend "sql1"
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
;; The schema-mapping is supplied and the... 
;;  X, F, T, and G 
;; ...should be derived from it.
;;
(def schema-perturbation
     {   :tables
      [ 
       {   :name "cot_action" 
           :columns 
           [    ["source" "name"]
                ["source" "channel"] 
                ["cot_event" "how"]
                ["cot_event" "servertime"]
                ["cot_position" "point_ce"]
                ["cot_position" "point_le"]
                ["cot_position" "tileX"]
                ["cot_position" "longitude"]
                ["cot_position" "latitude"]]}
       {   :name "cot_detail"
           :columns
            [   ["cot_position" "point_hae"]
                ["cot_event" "detail"]
                ["cot_position" "tileY"]
                ["cot_event" "cot_type"]]}]})


(def scx 
    {   :name "X"
        :type :schema
        :extend "sql1"
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
            

(def mapping-x->s 
    "A mapping between schema
    "
    {   :name "F"
        :type :mapping 
        :schemas ["X" "S"]
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
                    
                                         
(def sc1 
    {:name "T"
     :type :schema
     :extend "sql1"
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
     {  "has_cot_event" ["cot_action" "cot_detail"]
        "cot_action_idx" ["cot_detail" "cot_action"]
        "cot_action_idy" ["cot_action" "cot_detail"]}})

(def mapping-x->t 
   "A mapping between schema"
   {    :name "G"
        :type :mapping 
        :schemas ["X" "T"]
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
                {   "has_cot_event" "has_cot_event"
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
        
(def sql1 
    "typeside sql1 = literal {
        imports sql
        java_functions 
            EqualStr : String, String -> Bool = \"return input[0].equals(input[1])\"
            EqualVc : Varchar, Varchar -> Bool = \"return input[0].equals(input[1])\"
            EqualInt : Integer, Integer -> Bool = \"return input[0].equals(input[1])\"
        }")
         

(def q1x0 "query Qx = [ toQuery G ; toCoQuery F ]")

 ;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
 ;;  database/server/aql/src/aql/cp2_1_db.aql#L262

(def qs01 "query Qs_01 = simple : S  {
    from ce:cot_event
    where ce.cot_type = \"a-n-A-C-F-m\" 
    attributes 
        cot_type -> ce.cot_type 
        }")

(def qs01t "query Qt_01 = [ Qx ; Qs_01 ]")

(def qs02 
    "query Qs_02 = simple : S  {
    from ce:cot_event
    where ce.servertime = \"201705071635\" 
    attributes 
        cot_type -> ce.cot_type
        how -> ce.how 
    }")
    
(def qs02t "query Qt_02 = [ Qx ; Qs_02 ]")
    
    
