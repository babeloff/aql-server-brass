(ns aql.brass.query.demo-1
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  #::s
  {:key "Qs_01"
   :nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   :name "CotEventsForConstantCotType"
   :doc "
   ## Query 1 : cot_eventsForConstantCotType
   Basic test against single table with a simple filter on a projected column.
   In the original query the source_id was extracted by the query.
   In AQL foreign-key values are meaningless so extracting them is meaningless.
   If what is intended is some meaningful key indicating a row in the source entity;
   then a uuid on the source entity is probably what is meant.
   "
   :sql "
   	select ce.id, ce.source_id, ce.cot_type
   	from cot_event as ce
   	where ce.cot_type = 'a-n-A-C-F-s' ;
   "
   :source "
   query Qs_01 = simple : S  {
     from
      ce:cot_event
     where
      ce.cot_type = \"a-n-A-C-F-s\"
     attributes
      id -> ce.id
      source_id -> ce.source_id
      cot_type -> ce.cot_type
     }
   "
   :select-order ["id" "source_id" "cot_type"]
   :target "
   query Qt_01 = [ Qx ; Qs_01 ]
   "})
