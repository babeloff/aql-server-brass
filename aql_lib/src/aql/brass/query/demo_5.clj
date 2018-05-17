(ns aql.brass.query.demo-5
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  {::s/key "Qs_05"
   ::s/nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::s/name "CotEventsForConstantChannelJoin2"
   ::s/doc "
   Same as query4 but no projection of column from joined table.
   "
   ::s/sql "
     select ce.id, ce.cot_type, ce.servertime
     from source as s
       join cot_event as ce on s.source_id = ce.source_id
     where s.channel = '7'
   "
   ::s/source "
     query Qs_05 = simple : S {
            from
              s : source
              ce : cot_event
            where
              s = ce.source_fk
              s.channel = \"7\"
            attributes
              event_id -> ce.id
              cot_type -> ce.cot_type
              servertime -> ce.servertime
     }
   "
   ::s/select-order ["event_id" "cot_type" "servertime"]
   ::s/target "
   query Qt_05 = [ Qx ; Qs_05 ]
   "})
