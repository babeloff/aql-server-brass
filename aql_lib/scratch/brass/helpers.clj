(require '[clojure.pprint :as pp])
(require '[clojure.zip :as zip])
(require '(aql [wrap :as aql-wrap]))
(require '(com.rpl [specter :as sr]))

(def permute-sample
  {"cot_action"
   {::aql-wrap/pk "id"
    ::aql-wrap/fk {"source_fk" "source_id"
                   "cot_detail_fk" "id"}}
   "cot_detail"
   {::aql-wrap/pk "id"
    ::aql-wrap/fk {"cot_action_fk" "id"}}})

(def helpers
  {::aql-wrap/ref-alias
   {"source"
    {::aql-wrap/pk "source_id"}
    "cot_event"
    {::aql-wrap/pk "id"
     ::aql-wrap/fk {"source_fk" "source_id"}}
    "cot_event_position"
    {::aql-wrap/pk "id"
     ::aql-wrap/fk {"cot_event_fk" "cont_event_id"}}}
   ::aql-wrap/sort-select-fn :sort-select-junk})

(def helper-zip (zip/seq-zip helpers))

(def foo
  {"cot_action"
   {::aql-wrap/pk "id"
    ::aql-wrap/fk {"source_fk" "source_id"
                   "cot_detail_fk" "id"}}
   "cot_detail"
   {::aql-wrap/pk "id"
    ::aql-wrap/fk {"cot_action_fk" "id"}}})

(def merged
  (sr/transform [::aql-wrap/ref-alias]
                #(merge % foo)
                helpers))

(pp/pprint merged)
