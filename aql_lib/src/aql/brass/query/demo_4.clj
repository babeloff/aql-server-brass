(ns aql.brass.query.demo-4
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  {::s/key "Qs_04"
   ::s/nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::s/name "CotEventsForConstantChannelJoin"
   ::s/doc "
   Simple join with filter
   "
   ::s/sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
   join cot_event as ce on s.source_id = ce.source_id
   where s.channel = '7'
   "
   ::s/source "
   query Qs_04 = simple : S {
      from
        ce : cot_event
        s : source
      where
        s = ce.source_fk
        s.channel = \"7\"
      attributes
        name -> s.name
        event_id -> ce.id
        cot_type -> ce.cot_type
        servertime -> ce.servertime
   }
   "
   ::s/select-order ["name" "event_id" "cot_type" "servertime"]
   ::s/target "
   query Qt_04 = [ Qx ; Qs_04 ]
   "})
