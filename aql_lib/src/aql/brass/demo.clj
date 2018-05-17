;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.brass.demo
  (:require (aql [spec :as s])
            (aql.brass.query [spec :as dq]
                             [demo-1 :as dq1]
                             [demo-2 :as dq2]
                             [demo-3 :as dq3]
                             [demo-4 :as dq4]
                             [demo-5 :as dq5]
                             [demo-6 :as dq6]
                             [demo-7 :as dq7]
                             [demo-8 :as dq8])))

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

     ["cot_event_id" "cot_event_position" "Varchar"]
     ["point_hae" "cot_event_position" "Varchar"]
     ["point_ce" "cot_event_position" "Varchar"]
     ["point_le" "cot_event_position" "Varchar"]
     ["tilex" "cot_event_position" "Varchar"]
     ["tiley" "cot_event_position" "Varchar"]
     ["longitude" "cot_event_position" "Varchar"]
     ["latitude" "cot_event_position" "Varchar"]]
    :references
    [["source_fk" "cot_event" "source"]
     ["cot_event_fk" "cot_event_position" "cot_event"]]
    :observations
    [[["x" "cot_event"]
      [::s/equal
       ["source_id" "x"]
       ["source_id" ["source_fk" "x"]]]]
     [["y" "cot_event_position"]
      [::s/equal
       ["cot_event_id" "y"]
       ["id" ["cot_event_fk" "y"]]]]]})

(def schema-x
  #::s
   {:name "X"
    :type ::s/schema
    :extend "sql1"
    :entities
    #{"source" "cospan"}
    :attributes
    [["source_id" "source" "Varchar"]
     ["name" "source" "Varchar"]
     ["channel" "source" "Varchar"]

     ["cot_event_id" "cospan" "Varchar"]
     ["source_id" "cospan" "Varchar"]
     ["cot_type" "cospan" "Varchar"]
     ["how" "cospan" "Varchar"]
     ["detail" "cospan" "Varchar"]
     ["servertime" "cospan" "Varchar"]

     ["point_hae" "cospan" "Varchar"]
     ["point_ce" "cospan" "Varchar"]
     ["point_le" "cospan" "Varchar"]
     ["tilex" "cospan" "Varchar"]
     ["tiley" "cospan" "Varchar"]
     ["latitude" "cospan" "Varchar"]
     ["longitude" "cospan" "Varchar"]]
    :references
    [["source_fk" "cospan" "source"]]
    :observations
    [[["x" "cospan"]
      [::s/equal
       ["source_id" "x"]
       ["source_id" ["source_fk" "x"]]]]]})

(def mapping-f
  "A mapping between schema"
  #::s
   {:name "F"
    :type ::s/mapping
    :schema-map ["S" "X"]
    :entity-map
    {[["source"] ["source"]]
     #::s
     {:attribute-map
      {"source_id" "source_id"
       "name" "name"
       "channel" "channel"}}

     [["cot_event"] ["cospan"]]
     #::s
     {:reference-map {"source_fk" "source_fk"}
      :attribute-map
      {"id" "cot_event_id"
       "source_id" "source_id"
       "cot_type" "cot_type"
       "how" "how"
       "detail" "detail"
       "servertime" "servertime"}}

     [["cot_event_position"] ["cospan"]]
     #::s
     {:reference-map {"cot_event_fk" nil}
      :attribute-map
      {"cot_event_id" "cot_event_id"
       "point_hae" "point_hae"
       "point_ce" "point_ce"
       "point_le" "point_le"
       "tilex" "tilex"
       "tiley" "tiley"
       "latitude" "latitude"
       "longitude" "longitude"}}}})

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
  [(::dq/source dq1/qs) (::dq/target dq1/qs)
   (::dq/source dq2/qs) (::dq/target dq2/qs)
   (::dq/source dq3/qs) (::dq/target dq3/qs)
   (::dq/source dq4/qs) (::dq/target dq4/qs)
   ; (::dq/source-alt dq/qs-05) (::dq/target-alt dq/qs-05)
   (::dq/source dq5/qs) (::dq/target dq5/qs)
   (::dq/source dq6/qs) (::dq/target dq6/qs)
   (::dq/source dq7/qs) (::dq/target dq7/qs)
   dq8/sc
   (::dq/source-pre dq8/qs) (::dq/target-pre dq8/qs)
   (::dq/source dq8/qs) (::dq/target dq8/qs)])

(def query-dict
  {"Qs_01" dq1/qs
   "Qs_02" dq2/qs
   "Qs_03" dq3/qs
   "Qs_04" dq4/qs
   "Qs_05" dq5/qs
   "Qs_06" dq6/qs
   "Qs_07" dq7/qs
   "Qs_08pre" dq8/qs
   "Qt_01" dq1/qs
   "Qt_02" dq2/qs
   "Qt_03" dq3/qs
   "Qt_04" dq4/qs
   "Qt_05" dq5/qs
   "Qt_06" dq6/qs
   "Qt_07" dq7/qs
   "Qt_08pre" dq8/qs})

(defn qname [query]
  (str (::dq/nspace query) "." (::dq/name query)))

(def query-class-names
  (into (sorted-map)
        (map #(vector % (qname (get query-dict %))))
        ["Qt_01" "Qt_02" "Qt_03"
         "Qt_04" "Qt_05" "Qt_06"
         "Qt_07" "Qt_08pre"]))

(def demo-mutants
  "a list of the queries to return"
  {:query [[(qname dq1/qs) "Qs_01" "Qt_01"]
           [(qname dq2/qs) "Qs_02" "Qt_02"]
           [(qname dq3/qs) "Qs_03" "Qt_03"]
           [(qname dq4/qs) "Qs_04" "Qt_04"]
           [(qname dq5/qs) "Qs_05" "Qt_05"]
           [(qname dq6/qs) "Qs_06" "Qt_06"]
           [(qname dq7/qs) "Qs_07" "Qt_07"]
           [(qname dq8/qs) "Qs_08pre" "Qt_08pre"]]})
