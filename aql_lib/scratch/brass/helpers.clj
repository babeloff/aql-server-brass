(require '[clojure.pprint :as pp])
(require '[clojure.zip :as zip])
(require '(aql [wrap :as aw]))
(require '(com.rpl [specter :as sr]))

(def helpers
  {::aw/ref-alias
   {"source"
    {::aw/pk "source_id"}
    "cot_event"
    {::aw/pk "id"
     ::aw/fk {"source_fk" "source_id"}}
    "cot_event_position"
    {::aw/pk "id"
     ::aw/fk {"cot_event_fk" "cont_event_id"}}}
   ::aw/sort-select-fn :sort-select-junk
   ::aw/tweek-output-xf :tweek-query-output})

(def helper-zip (zip/seq-zip helpers))

(def foo
  {"cot_action"
   {::aw/pk "id"
    ::aw/fk {"source_fk" "source_id"
             "cot_detail_fk" "id"}}
   "cot_detail"
   {::aw/pk "id"
    ::aw/fk {"cot_action_fk" "id"}}})

(def merged
  (sr/transform [::aw/ref-alias]
                #(merge % foo)
                helpers))

(pp/pprint merged)
