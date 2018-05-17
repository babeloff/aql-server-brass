(ns aql.brass.query.demo-7
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  {::s/key "Qs_07"
   ::s/nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::s/name "CotEventsOnChannelInRegion"
   ::s/doc "
   More complex join and filter
   "
   ::s/sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
     join cot_event as ce on s.source_id = ce.source_id
     join cot_event_position cep on ce.id = cep.cot_event_fk
   where  s.channel = '6' and cep.tilex = '18830' and cep.tiley = '25704'
   "
   ::s/source "
   query Qs_07 = simple : S {
     from
       ce : cot_event
       cep : cot_event_position
       s : source
     where
       s = ce.source_fk
       ce = cep.cot_event_fk
       s.channel = \"6\"
       cep.tilex = \"18830\"
       cep.tiley = \"25704\"
     attributes
       name -> s.name
       event_id -> ce.id
       cot_type -> ce.cot_type
       servertime -> ce.servertime
     }
   "
   ::s/select-order ["name" "event_id" "cot_type" "servertime"]
   ::s/target "
   query Qt_07 = [ Qx ; Qs_07 ]
   "})
