(ns aql.brass.demo-gen
  (:require (aql [spec :as s])))


(def schema-t
  #::s
   {:name "T"
    :type ::s/schema
    :extend "sql1"
    :entities
    #{"source" "cot_action" "cot_detail"}
    :attributes
    [["id" "source" "Varchar"]
     ["name" "source" "Varchar"]
     ["channel" "source" "Varchar"]

     ["id" "cot_action" "Varchar"]
     ["how" "cot_action" "Varchar"]
     ["servertime" "cot_action" "Varchar"]
     ["point_ce" "cot_action" "Varchar"]
     ["point_le" "cot_action" "Varchar"]
     ["tilex" "cot_action" "Varchar"]
     ["latitude" "cot_action" "Varchar"]
     ["longitude" "cot_action" "Varchar"]

     ["id" "cot_detail" "Varchar"]
     ["detail" "cot_detail" "Varchar"]
     ["cot_type" "cot_detail" "Varchar"]
     ["tiley" "cot_detail" "Varchar"]
     ["point_hae" "cot_detail" "Varchar"]]
    :references
    [["source_fk" "cot_action" "source"]
     ["cot_action_fk" "cot_detail" "cot_action"]
     ["cot_detail_fk" "cot_action" "cot_detail"]]
    :observations
    [[["x" "cot_action"]
      [::s/equal
       ["source_id" "x"]
       ["id" ["source_fk" "x"]]]]
     [["y" "cot_detail"]
      [::s/equal
       ["cot_event_id" "y"]
       ["id" ["cot_event_fk" "y"]]]]]})

(def mapping-g
  "A mapping between schema"
  #::s
   {:name "G"
    :type ::s/mapping
    :schema-map ["T" "X"]
    :entity-map
    {[["source"] ["source"]]
     #::s
     {:attribute-map
      {"id" "source_id"
       "name" "name"
       "channel" "channel"}}
     [["cot_action"] ["cospan"]]
     #::s
     {:reference-map
      {"cot_detail_fk" nil
       "source_fk" nil}
      :attribute-map
      {"id" "cot_event_id"
       "source_id" "source_id"
       "how" "how"
       "servertime" "servertime"
       "point_ce" "point_ce"
       "point_le" "point_le"
       "tilex" "tilex"
       "longitude" "longitude"
       "latitude" "latitude"}}
     [["cot_detail"] ["cospan"]]
     #::s
     {:reference-map
      {"cot_action_fk" nil}
      :attribute-map
      {"id" "id"
       "cot_event_id" "cot_event_id"
       "point_hae" "point_hae"
       "detail" "detail"
       "tiley" "tiley"
       "cot_type" "cot_type"}}}})
