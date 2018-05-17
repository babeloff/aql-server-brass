(ns aql.brass.query.demo-6
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def qs
  {::s/key "Qs_06"
   ::s/nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::s/name "CotEventsForConstantMixedJoin"
   ::s/doc "
   Same as query5 except join across tables.
   "
   ::s/sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
     join cot_event as ce on s.source_id = ce.source_id
   where  s.channel = '5'
     or ce.cot_type = 'a-n-A-C-F-s' ;
   "
   ::s/source "
   query Qs_06 = simple : S {
     from
       ce : cot_event
       s : source
     where
       s = ce.source_fk
       OrBool(EqualVc(s.channel,\"5\"),
              EqualVc(ce.cot_type,\"a-n-A-C-F-s\")) = true
     attributes
       name -> s.name
       id -> ce.id
       cot_type -> ce.cot_type
       servertime -> ce.servertime
   }
   "
   ::s/select-order ["name" "id" "cot_type" "servertime"]
   ::s/target "
   query Qt_06 = [ Qx ; Qs_06 ]
   "})
