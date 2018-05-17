(ns aql.brass.query.demo-2
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  #::s
  {:key "Qs_02"
   :nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   :name "CotEventsForConstantTimeInterval"
   :doc "
   Like query 1 except filter on non-projected column.
   "
   :sql "
   select ce.id, ce.source_id, ce.cot_type, ce.how
   from cot_event as ce
   where ce.servertime = '201705071635' ;
   "
   :source "
   query Qs_02 = simple : S  {
     from
      ce:cot_event
     where
      ce.servertime = \"201705071635\"
     attributes
      id -> ce.id
      source_id -> ce.source_id
      cot_type -> ce.cot_type
      how -> ce.how
     }
   "
   :select-order ["id" "source_id" "cot_type" "how"]
   :target "
   query Qt_02 = [ Qx ; Qs_02 ]
   "})
