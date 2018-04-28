(ns aql.brass.data-query
  "The BRASS IMMoRTALS demo queries"
  (:require (aql [spec :as s])))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk
;;   /database/server/baseline_schema_ddl.sql
;;
;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk
;;   /shared/modules/dfus/TakServerDataManager/src/main/java
;;     /mil/darpa/immortals/dfus/TakServerDataManager/DFU

(def qs-01
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForConstantCotType"
   ::doc "
   ## Query 1 : cot_eventsForConstantCotType
   Basic test against single table with a simple filter on a projected column.
   In the original query the source_id was extracted by the query.
   In AQL foreign-key values are meaningless so extracting them is meaningless.
   If what is intended is some meaningful key indicating a row in the source entity;
   then a uuid on the source entity is probably what is meant.
   "
   ::sql "
   	select ce.id, ce.source_id, ce.cot_type
   	from cot_event as ce
   	where ce.cot_type = 'a-n-A-C-F-s'
   "
   ::source "
   query Qs_01 = simple : S  {
     from
      ce:cot_event
     where
      ce.cot_type = \"a-n-A-C-F-s\"
     attributes
      event_id -> ce.id
      source_id -> ce.source_id
      cot_type -> ce.cot_type
     }
   "
   ::select-order ["event_id" "source_id" "cot_type"]
   ::target "
   query Qt_01 = [ Qx ; Qs_01 ]
   "})

(def qs-02
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForConstantTimeInterval"
   ::doc "
   Like query 1 except filter on non-projected column.
   "
   ::sql "
   select ce.id, ce.source_id, ce.cot_type, ce.how
   from cot_event as ce
   where ce.servertime = '201705071635'
   "
   ::source "
   query Qs_02 = simple : S  {
     from
      ce:cot_event
     where
      ce.servertime = \"201705071635\"
     attributes
      event_id -> ce.id
      source_id -> ce.source_id
      cot_type -> ce.cot_type
      how -> ce.how
     }
   "
   ::select-order ["event_id" "source_id" "cot_type" "how"]
   ::target "
   query Qt_02 = [ Qx ; Qs_02 ]
   "})

(def qs-03
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForConstantCompoundFilter"
   ::doc "
   Query with a simple compound filter
   "
   ::sql "
   select ce.id, ce.source_id, ce.cot_type, ce.how
  	from cot_event as ce
  	where ce.servertime = '201705071635'
  	and ce.cot_type = 'a-n-A-C-F-m'
   "
   ::source "
   query Qs_03 = simple : S {
      from ce : cot_event
      where
        ce.servertime = \"201705071635\"
        ce.cot_type = \"a-n-A-C-F-m\"
      attributes
        event_id -> ce.id
        source_id -> ce.source_id
        cot_type -> ce.cot_type
        how -> ce.how
      }
   "
   ::select-order ["event_id" "source_id" "cot_type"]
   ::target "
   query Qt_03 = [ Qx ; Qs_03 ]
   "})

(def qs-04
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForConstantChannelJoin"
   ::doc "
   Simple join with filter
   "
   ::sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
   join cot_event as ce on s.source_id = ce.source_id
   where s.channel = '7'
   "
   ::source "
   query Qs_04 = simple : S {
      from
        ce : cot_event
        s : source
      where
        s = ce.has_source
        s.channel = \"7\"
      attributes
        name -> s.name
        event_id -> ce.id
        cot_type -> ce.cot_type
        servertime -> ce.servertime
   }
   "
   ::select-order ["name" "event_id" "cot_type" "servertime"]
   ::target "
   query Qt_04 = [ Qx ; Qs_04 ]
   "})

(def qs-05
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForConstantChannelJoin2"
   ::doc "
   Same as query4 but no projection of column from joined table.
   "
   ::sql "
     select ce.id, ce.cot_type, ce.servertime
     from source as s
       join cot_event as ce on s.source_id = ce.source_id
     where s.channel = '7'
   "
   ::source "
     query Qs_05 = simple : S {
            from
              s : source
              ce : cot_event
            where
              s = ce.has_source
              s.channel = \"7\"
            attributes
              event_id -> ce.id
              type -> ce.cot_type
              channel -> s.channel
              time -> ce.servertime
     }
   "
   ::select-order ["id" "cot_type" "servertime"]
   ::target "
   query Qt_05 = [ Qx ; Qs_05 ]
   "})

(def qs-06
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForConstantMixedJoin"
   ::doc "
   Same as query5 except join across tables.
   "
   ::sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
     join cot_event as ce on s.source_id = ce.source_id
   where  s.channel = '5'
     or ce.cot_type = 'a-n-A-C-F-s'
   "
   ::source "
   query Qs_06s = simple : S {
     from
       ce : cot_event
       s : source
     where
       s = ce.has_source
       OrBool(EqualVc(s.channel,\"5\"),
              EqualVc(ce.cot_type,\"a-n-A-C-F-s\")) = true
     attributes
       name -> s.name
       event_id -> ce.id
       cot_type -> ce.cot_type
       time -> ce.servertime
   }
   "
   ::select-order ["name" "event_id" "cot_type" "servertime"]
   ::target "
   query Qt_06s = [ Qx ; Qs_06s ]
   "})

(def qs-07
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsOnChannelInRegion"
   ::doc "
   More complex join and filter
   "
   ::sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
     join cot_event as ce on s.source_id = ce.source_id
     join cot_event_position cep on ce.id = cep.has_cot_event
   where  s.channel = '6' and cep.tilex = '18830' and cep.tiley = '25704'
   "
   ::source "
   query Qs_07s = simple : S {
     from
       ce : cot_event
       cep : cot_event_position
       s : source
     where
       s = ce.has_source
       ce = cep.has_cot_event
       s.channel = \"6\"
       cep.tilex = \"18830\"
       cep.tiley = \"25704\"
     attributes
       name -> s.name
       cot_type -> ce.cot_type
       time -> ce.servertime
     }
   "
   ::select-order ["name" "event_id" "cot_type" "servertime"]
   ::target "
   query Qt_07s = [ Qx ; Qs_07s ]
   "})

(def sc-08 "
  schema S8 = literal : sql1 {
     entities
           Q
       attributes
           source_id : Q -> Varchar
           name : Q -> Varchar
           event_id : Q -> Varchar
           time : Q -> Varchar
           tilex : Q -> Varchar
           tiley : Q -> Varchar
   }
  ")

(def qs-08
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForUidAndInterval"
   ::doc "
   Simple parameterized query.
   "
   ::sql-pre "
   select s.id, s.name, ce.id, ce.servertime, cep.tilex, cep.tiley
   from source as s
     join cot_event as ce on s.id = ce.source_id
     join cot_event_position cep on ce.id = cep.cot_event_id
   where s.name = ? and ce.servertime = ?
   "
   ::sql "
   "
   ::source-pre "
   query Qs_08pre = literal : S -> S8 {
      params
         name_parm : Varchar
         servertime_parm : Varchar
      entity
        Q -> {
          from
            s : source
            ce : cot_event
            cep : cot_event_position
          where
            s = ce.has_source
            ce = cep.has_cot_event
            s.name = name_parm
            ce.servertime = servertime_parm
          attributes
            source_id -> s.source_id
            name -> s.name
            event_id -> ce.id
            time -> ce.servertime
            tilex -> cep.tilex
            tiley -> cep.tiley
        }
     }
   "
   ::source "
   query Qs_08 = literal : S -> S8 {
      bindings
         name_parm = \"A6A7DC\"
         servertime_parm = \"1494174900\"
      imports Qs_08pre
   }"
   ::select-order ["source_id" "name" "event_id" "time" "tilex" "tiley"]
   ::target-pre "
   query Qt_08pre = [ Qx ; Qs_08pre ]
   "
   ::target "
   query Qt_08 = [ Qx ; Qs_08 ]
   "})

(def sc-09 "
  schema S9 = literal : sql1 {
     entities
           Q
       attributes
           source_id : Q -> Varchar
           name : Q -> Varchar
           event_id : Q -> Varchar
           time : Q -> Varchar
           tilex : Q -> Varchar
           tiley : Q -> Varchar
     }
")

(def qs-09
  {::nspace "mil.darpa.immortals.dfus.TakServerDataManager.DFU"
   ::name "CotEventsForUidAndInterval"
   ::doc "
   Compare to query 8 except trained using bound parameters.
   Effectively treating it as a canned query but
   attributesing all results from sample parameter binding.
   "
   ::sql-pre "
   select s.id, s.name, ce.servertime, cep.tilex, cep.tiley
   from source as s
     join cot_event as ce on s.id = ce.source_id
     join cot_event_position cep on ce.id = cep.has_cot_event
   where s.name = ? and ce.servertime = ?
   "
   ::sql "
   where s.name = 'ABD19E' and servertime = 1494174900
   "
   ::source-pre "
   query Qs_09pre = literal : S -> S9 {
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
            s = ce.has_source
            ce = cep.has_cot_event
            s.name = name_param
            ce.servertime = servertime_param
          attributes
            source_id -> s.source_id
            name -> s.name
            event_id -> ce.id
            time -> ce.servertime
            tilex -> cep.tilex
            tiley -> cep.tiley
        }
     }
   "
   ::source "
   query Qs_09 = literal : S -> S9 {
      bindings
         name_param = \"A6A7DC\"
         servertime_param = \"1494174900\"

      imports Qs_09pre
   }
   "
   ::select-order ["source_id" "name" "event_id" "time" "tilex" "tiley"]
   ::target-pre "
   query Qt_09pre = [ Qx ; Qs_09pre ]
   "
   ::target "
   query Qt_09 = [ Qx ; Qs_09 ]
   "})
