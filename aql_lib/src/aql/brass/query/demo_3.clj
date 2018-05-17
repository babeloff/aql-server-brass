(ns aql.brass.query.demo-3
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  #::s
  {:key "Qs_03"
   :nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   :name "CotEventsForConstantCompoundFilter"
   :doc "
   Query with a simple compound filter
   "
   :sql "
   select ce.id, ce.source_id, ce.cot_type, ce.how
  	from cot_event as ce
  	where ce.servertime = '201705071635'
  	and ce.cot_type = 'a-n-A-C-F-m' ;
   "
   :source "
   query Qs_03 = simple : S {
      from ce : cot_event
      where
        ce.servertime = \"201705071635\"
        ce.cot_type = \"a-n-A-C-F-m\"
      attributes
        id -> ce.id
        source_id -> ce.source_id
        cot_type -> ce.cot_type
        how -> ce.how
      }
   "
   :select-order ["id" "source_id" "cot_type" "how"]
   :target "
   query Qt_03 = [ Qx ; Qs_03 ]
   "})
