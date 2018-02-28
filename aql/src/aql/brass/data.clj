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
   #{"source" "cot_event" "cot_position"}
   :attributes
   [["id" "source" "Integer"]
    ["name" "source" "Varchar"]
    ["channel" "source" "Varchar"]

    ["id" "cot_event" "Integer"]
    ["source_id" "cot_event" "Integer"]
    ["cot_type" "cot_event" "Varchar"]
    ["how" "cot_event" "Varchar"]
    ["detail" "cot_event" "Text"]
    ["servertime" "cot_event" "Bigint"]

    ["id" "cot_position" "Integer"]
    ["cot_event_id" "cot_position" "Integer"]
    ["point_hae" "cot_position" "Integer"]
    ["point_ce" "cot_position" "Integer"]
    ["point_le" "cot_position" "Integer"]
    ["tileX" "cot_position" "Integer"]
    ["tileY" "cot_position" "Integer"]
    ["latitude" "cot_position" "Real"]
    ["longitude" "cot_position" "Real"]]
   :references
   [["has_source" "cot_event" "source"]
    ["has_cot_event" "cot_position" "cot_event"]]
   :observations
   [[["x" "cot_event"]
     [::s/equal
      ["source_id" "x"]
      ["id" ["has_source" "x"]]]]
    [["y" "cot_position"]
     [::s/equal
      ["cot_event_id" "y"]
      ["id" ["has_cot_event" "y"]]]]]})

(def schema-x
  #::s
  {:name "X"
   :type ::s/schema
   :extend "sql1"
   :entities
   #{"cot_cospan"}
   :attributes
   [["id" "cot_cospan" "Integer"]
    ["name" "cot_cospan" "Varchar"]
    ["channel" "cot_cospan" "Varchar"]

    ["source_id" "cot_cospan" "Integer"]
    ["cot_type" "cot_cospan" "Varchar"]
    ["how" "cot_cospan" "Varchar"]
    ["detail" "cot_cospan" "Text"]
    ["servertime" "cot_cospan" "Bigint"]

    ["cot_event_id" "cot_cospan" "Integer"]
    ["point_hae" "cot_cospan" "Integer"]
    ["point_ce" "cot_cospan" "Integer"]
    ["point_le" "cot_cospan" "Integer"]
    ["tileX" "cot_cospan" "Integer"]
    ["tileY" "cot_cospan" "Integer"]
    ["latitude" "cot_cospan" "Real"]
    ["longitude" "cot_cospan" "Real"]]})

(def mapping-s->x
  "A mapping between schema"
  #::s
  {:name "F"
   :type ::s/mapping
   :schema-map ["S" "X"]
   :entity-map
   {[["source"] ["cot_cospan"]]
    #::s
    {:attribute-map
     {"id" "source_id"
      "name" "name"
      "channel" "channel"}}

    [["cot_event"] ["cot_cospan"]]
    #::s
    {:reference-map {"has_source" nil}
     :attribute-map
     {"id" "cot_event_id"
      "source_id" "source_id"
      "cot_type" "cot_type"
      "how" "how"
      "detail" "detail"
      "servertime" "servertime"}}

    [["cot_position"] ["cot_cospan"]]
    #::s
    {:reference-map {"has_cot_event" nil}
     :attribute-map
     {"id" "id"
      "cot_event_id" "cot_event_id"
      "point_hae" "point_hae"
      "point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "tileY" "tileY"
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
   [["name" "source" "Varchar"]
    ["channel" "source" "Varchar"]

    ["how" "cot_action" "Varchar"]
    ["servertime" "cot_action" "Bigint"]
    ["point_ce" "cot_action" "Integer"]
    ["point_le" "cot_action" "Integer"]
    ["tileX" "cot_action" "Integer"]
    ["latitude" "cot_action" "Real"]
    ["longitude" "cot_action" "Real"]

    ["detail" "cot_detail" "Text"]
    ["cot_type" "cot_detail" "Varchar"]
    ["tileY" "cot_detail" "Integer"]
    ["point_hae" "cot_detail" "Integer"]]
   :references
   [["has_source" "cot_action" "source"]
    ["has_cot_action_idx" "cot_detail" "cot_action"]
    ["has_cot_action_idy" "cot_action" "cot_detail"]]})


(def mapping-t->x
  "A mapping between schema"
  #::s
  {:name "G"
   :type ::s/mapping
   :schema-map ["T" "X"]
   :entity-map
   {
     [["entity source"] ["cot_cospan"]]
     {:attribute-map
       {"name" "name"
        "channel" "channel"}}
    [["cot_action"] ["cot_cospan"]]
    #::s
    {:reference-map
     {"has_cot_detail" nil
      "source_id" nil}
     :attribute-map
     {"how" "how"
      "servertime" "servertime"
      "point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "longitude" "longitude"
      "latitude" "latitude"}}
    [["cot_detail"] ["cot_cospan"]]
    #::s
    {:reference-map
     {"has_cot_action" nil}
     :attribute-map
     {"point_hae" "point_hae"
      "detail" "detail"
      "tileY" "tileY"
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
          EqualInt : Integer, Integer -> Bool = \"return input[0].equals(input[1])\"
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
   dq/sc-05
   (::dq/source-alt dq/qs-05) (::dq/target-alt dq/qs-05)
   (::dq/source dq/qs-05) (::dq/target dq/qs-05)
   (::dq/source dq/qs-06) (::dq/target dq/qs-06)
   (::dq/source dq/qs-07) (::dq/target dq/qs-07)
   dq/sc-08
   (::dq/source-pre dq/qs-08) (::dq/target-pre dq/qs-08)
   (::dq/source dq/qs-08) (::dq/target dq/qs-08)
   dq/sc-09
   (::dq/source-pre dq/qs-09) (::dq/target-pre dq/qs-09)
   (::dq/source dq/qs-09) (::dq/target dq/qs-09)])

(def query-class-names
  { "Qt_01" (::dq/qname dq/qs-01)
    "Qt_02" (::dq/qname dq/qs-02)
    "Qt_03" (::dq/qname dq/qs-03)
    "Qt_04" (::dq/qname dq/qs-04)
    "Qt_05" (::dq/qname dq/qs-05)
    "Qt_06s" (::dq/qname dq/qs-06)
    "Qt_07s" (::dq/qname dq/qs-07)
    "Qt_08pre" (::dq/qname dq/qs-08)
    "Qt_09pre" (::dq/qname dq/qs-09)})

(defn query-tweeker
  "a transducer that 'tweeks' the key value
  to be the qname for the class."
  [xf]
  (fn
    ([] (xf))
    ([res] (xf res))
    ([res [k v]]
     (xf res (vector (get query-class-names k k) v k)))))


(def query-demo-attributes
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
