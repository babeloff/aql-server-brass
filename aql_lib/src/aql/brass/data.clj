;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.brass.data
  (:require (aql [spec :as s])
            (aql.brass [data-query :as dq])))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   database/server/baseline_schema_ddl.sql

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"


;; The schema-mapping is supplied and the...
;;  X, F, T, and G
;; ...should be derived from it.
;;
;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   das/das-testharness-coordinator/src/main/java/mil/darpa/immortals/
;;   core/api/ll/phase2/martimodel/requirements/storage/postgresql/DatabaseColumns.java
(def schema-s
  #::s
   {:name "S"
    :type ::s/schema
    :extend "sql1"
    :entities
    #{"source" "cot_event" "cot_event_position"}
    :attributes
    [["source_id" "source" "Varchar"]
     ["name" "source" "Varchar"]
     ["channel" "source" "Varchar"]

     ["id" "cot_event" "Varchar"]
     ["source_id" "cot_event" "Varchar"]
     ["cot_type" "cot_event" "Varchar"]
     ["how" "cot_event" "Varchar"]
     ["detail" "cot_event" "Varchar"]
     ["servertime" "cot_event" "Varchar"]

     ["id" "cot_event_position" "Varchar"]
     ["cot_event_id" "cot_event_position" "Varchar"]
     ["point_hae" "cot_event_position" "Varchar"]
     ["point_ce" "cot_event_position" "Varchar"]
     ["point_le" "cot_event_position" "Varchar"]
     ["tilex" "cot_event_position" "Varchar"]
     ["tiley" "cot_event_position" "Varchar"]
     ["latitude" "cot_event_position" "Varchar"]
     ["longitude" "cot_event_position" "Varchar"]]
    :references
    [["has_source" "cot_event" "source"]
     ["has_cot_event" "cot_event_position" "cot_event"]]
    :observations
    [[["x" "cot_event"]
      [::s/equal
       ["source_id" "x"]
       ["source_id" ["has_source" "x"]]]]
     [["y" "cot_event_position"]
      [::s/equal
       ["cot_event_id" "y"]
       ["id" ["has_cot_event" "y"]]]]]})

(def schema-x
  #::s
   {:name "X"
    :type ::s/schema
    :extend "sql1"
    :entities
    #{"cospan"}
    :attributes
    [["source_id" "cospan" "Varchar"]
     ["name" "cospan" "Varchar"]
     ["channel" "cospan" "Varchar"]

     ["cot_event_id" "cospan" "Varchar"]
     ["cot_type" "cospan" "Varchar"]
     ["how" "cospan" "Varchar"]
     ["detail" "cospan" "Varchar"]
     ["servertime" "cospan" "Varchar"]

     ["cot_position_id" "cospan" "Varchar"]
     ["point_hae" "cospan" "Varchar"]
     ["point_ce" "cospan" "Varchar"]
     ["point_le" "cospan" "Varchar"]
     ["tilex" "cospan" "Varchar"]
     ["tiley" "cospan" "Varchar"]
     ["latitude" "cospan" "Varchar"]
     ["longitude" "cospan" "Varchar"]]})

(def mapping-f
  "A mapping between schema"
  #::s
   {:name "F"
    :type ::s/mapping
    :schema-map ["S" "X"]
    :entity-map
    {[["source"] ["cospan"]]
     #::s
     {:attribute-map
      {"source_id" "source_id"
       "name" "name"
       "channel" "channel"}}

     [["cot_event"] ["cospan"]]
     #::s
     {:reference-map {"has_source" nil}
      :attribute-map
      {"id" "cot_event_id"
       "source_id" "source_id"
       "cot_type" "cot_type"
       "how" "how"
       "detail" "detail"
       "servertime" "servertime"}}

     [["cot_event_position"] ["cospan"]]
     #::s
     {:reference-map {"has_cot_event" nil}
      :attribute-map
      {"id" "cot_position_id"
       "cot_event_id" "cot_event_id"
       "point_hae" "point_hae"
       "point_ce" "point_ce"
       "point_le" "point_le"
       "tilex" "tilex"
       "tiley" "tiley"
       "latitude" "latitude"
       "longitude" "longitude"}}}})

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
    [["has_source" "cot_action" "source"]
     ["has_cot_action" "cot_detail" "cot_action"]
     ["has_cot_detail" "cot_action" "cot_detail"]]
    :observations
    [[["x" "cot_action"]
      [::s/equal
       ["source_id" "x"]
       ["id" ["has_source" "x"]]]]
     [["y" "cot_detail"]
      [::s/equal
       ["cot_event_id" "y"]
       ["id" ["has_cot_event" "y"]]]]]})

(def mapping-g
  "A mapping between schema"
  #::s
   {:name "G"
    :type ::s/mapping
    :schema-map ["T" "X"]
    :entity-map
    {[["entity source"] ["cospan"]]
     #::s
     {:attribute-map
      {"id" "source_id"
       "name" "name"
       "channel" "channel"}}
     [["cot_action"] ["cospan"]]
     #::s
     {:reference-map
      {"has_cot_detail" nil
       "has_source" nil}
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
      {"has_cot_action" nil}
      :attribute-map
      {"id" "id"
       "cot_event_id" "cot_event_id"
       "point_hae" "point_hae"
       "detail" "detail"
       "tiley" "tiley"
       "cot_type" "cot_type"}}}})

(def ts-sql1
  "typeside sql1 = literal {
        imports sql
        java_types
          Geo = \"java.lang.Long\"
        java_constants
          Geo = \"return java.lang.Long.decode(input[0])\"
        java_functions
          EqualStr : String, String -> Bool = \"return input[0].equals(input[1])\"
          EqualVc : Varchar, Varchar -> Bool = \"return input[0].equals(input[1])\"
          EqualInt : Varchar, Varchar -> Bool = \"return input[0].equals(input[1])\"
          OrBool : Bool, Bool -> Bool = \"return input[0] || input[1]\"
        }")

(def qgf "query Qx = [ toCoQuery G ; toQuery F ]")

(def query-demo
  "all the queries for the demo
    Includes all the initial queries as well as the targets"
  [(::dq/source dq/qs-01) (::dq/target dq/qs-01)
   (::dq/source dq/qs-02) (::dq/target dq/qs-02)
   (::dq/source dq/qs-03) (::dq/target dq/qs-03)
   (::dq/source dq/qs-04) (::dq/target dq/qs-04)
   ; (::dq/source-alt dq/qs-05) (::dq/target-alt dq/qs-05)
   (::dq/source dq/qs-05) (::dq/target dq/qs-05)
   (::dq/source dq/qs-06) (::dq/target dq/qs-06)
   (::dq/source dq/qs-07) (::dq/target dq/qs-07)
   dq/sc-08
   (::dq/source-pre dq/qs-08) (::dq/target-pre dq/qs-08)
   (::dq/source dq/qs-08) (::dq/target dq/qs-08)
   dq/sc-09
   (::dq/source-pre dq/qs-09) (::dq/target-pre dq/qs-09)
   (::dq/source dq/qs-09) (::dq/target dq/qs-09)])

(defn qname [query]
  (str (::dq/nspace query) "." (::dq/name query)))

(def query-class-names
  {"Qt_01" (qname dq/qs-01)
   "Qt_02" (qname dq/qs-02)
   "Qt_03" (qname dq/qs-03)
   "Qt_04" (qname dq/qs-04)
   "Qt_05" (qname dq/qs-05)
   "Qt_06s" (qname dq/qs-06)
   "Qt_07s" (qname dq/qs-07)
   "Qt_08pre" (qname dq/qs-08)
   "Qt_09pre" (qname dq/qs-09)})

(def query-dict
  (into {}
        (map (fn [q] (vector (::dq/key q) q)))
        [dq/qs-01 dq/qs-02 dq/qs-03
         dq/qs-04 dq/qs-05 dq/qs-06
         dq/qs-07 dq/qs-08 dq/qs-09]))

(def demo-objects
  "a list of the queries to return"
  {:query ["Qs_01" "Qt_01"
           "Qs_02" "Qt_02"
           "Qs_03" "Qt_03"
           "Qs_04" "Qt_04"
           "Qs_05" "Qt_05"
           "Qs_06s" "Qt_06s"
           "Qs_07s" "Qt_07s"
           "Qs_08pre" "Qt_08pre"
           "Qs_08" "Qt_08"
           "Qs_09pre" "Qt_09pre"
           "Qs_09" "Qt_09"]})
