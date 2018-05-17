(ns aql.brass.query.demo-8
  "The BRASS IMMoRTALS demo queries"
  (:require (aql.brass.query [spec :as s])))

(def sc "
  schema S8 = literal : sql1 {
     entities
           Q
       attributes
           source_id : Q -> Varchar
           name : Q -> Varchar
           id : Q -> Varchar
           time : Q -> Varchar
           tilex : Q -> Varchar
           tiley : Q -> Varchar
   }
  ")

(def qs
  {::s/key "Qs_08"
   ::s/nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::s/name "CotEventsForUidAndInterval"
   ::s/doc "
   Simple parameterized query.
   "
   ::s/sql-pre "
   select s.source_id, s.name, ce.id, ce.servertime, cep.tilex, cep.tiley
   from source as s
     join cot_event as ce on s.source_id = ce.source_id
     join cot_event_position cep on ce.id = cep.cot_event_id
   where s.name = ? and ce.servertime = ? ;
   "
   ::s/sql "
   where s.name = 'ABD19E' and servertime = 1494174900
   "
   ::s/source-pre "
   query Qs_08pre = literal : S -> S8 {
      params
        name_param : Varchar
        servertime_param : Varchar
      entity
        Q -> {
          from
            s : source
            ce : cot_event
            cep : cot_event_position
          where
            s = ce.source_fk
            ce = cep.cot_event_fk
            s.name = name_param
            ce.servertime = servertime_param
          attributes
            source_id -> s.source_id
            name -> s.name
            id -> ce.id
            time -> ce.servertime
            tilex -> cep.tilex
            tiley -> cep.tiley
        }
     }
   "
   ::s/source "
   query Qs_08 = literal : S -> S8 {
      bindings
         name_param = \"A6A7DC\"
         servertime_param = \"1494174900\"

      imports Qs_08pre
   }
   "
   ::s/select-order ["source_id" "name" "id" "time" "tilex" "tiley"]
   ::s/target-pre "
   query Qt_08pre = [ Qx ; Qs_08pre ]
   "
   ::s/target "
   query Qt_08 = [ Qx ; Qs_08 ]
   "})
