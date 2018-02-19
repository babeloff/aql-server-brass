(ns aql.brass.data-query
  "The BRASS IMMoRTALS demo queries"
  (:require (aql [spec :as s])))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   database/server/baseline_schema_ddl.sql

(def qs-01
  {::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForConstantCotType"
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
      ce.cot_type = \"a-n-A-C-F-m\"
     attributes
      // source_id -> ce.source_id
      cot_type -> ce.cot_type
     }
   "
   ::target "
   query Qt_01 = [ Qx ; Qs_01 ]
   "})

(def qs-02
  {::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForConstantTimeInterval"
   ::doc "
   Like query 1 except filter on non-projected column.
   "
   ::sql "
   select ce.id, ce.source_id, ce.cot_type, ce.how
   from cot_event as ce
   where ce.servertime = 1494174900 // 2017-05-07t16:35
   "
   ::source "
   query Qs_02 = simple : S  {
     from
      ce:cot_event
     where
      ce.servertime = \"1494174900\"
     attributes
      // source_id -> ce.source_id
      cot_type -> ce.cot_type
      how -> ce.how
     }
   "
   ::target "
   query Qt_02 = [ Qx ; Qs_02 ]
   "})

(def qs-03
  {::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForConstantCompoundFilter"
   ::doc "
   Query with a simple compound filter
   "
   ::sql "
   select ce.id, ce.source_id, ce.cot_type, ce.how
  	from cot_event as ce
  	where ce.servertime = 1494174900
  	and ce.cot_type = 'a-n-A-C-F-m'
   "
   ::source "
   query Qs_03 = simple : S {
      from ce : cot_event
      where
        ce.servertime = \"1494174900\"
        ce.cot_type = \"a-n-A-C-F-m\"
      attributes
        // source_id -> ce.source_id
        cot_type -> ce.cot_type
        how -> ce.how
      }
   "
   ::target "
   query Qt_03 = [ Qx ; Qs_03 ]
   "})

(def qs-04
  {::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForConstantChannelJoin"
   ::doc "
   Simple join with filter
   "
   ::sql "
   select s.name, ce.id, ce.cot_type, ce.servertime
   from source as s
   join cot_event as ce on s.id = ce.source_id
   where s.channel = 7
   "
   ::source "
   query Qs_04 = simple : S {
      from
        ce : cot_event
        s : source
      where
        s = ce.source_id
        s.channel = \"7\"
      attributes
        name -> s.name
        cot_type -> ce.cot_type
        time -> ce.servertime
   }
   "
   ::target "
   query Qt_04 = [ Qx ; Qs_04 ]
   "})

(def sc-05 "
  schema S5m = literal : sql {
    entities
          Qchan
          Qtype
      attributes
          name : Qchan -> Varchar
          time : Qchan -> Bigint
          type : Qchan -> Varchar
          channel : Qchan -> Varchar

          name : Qtype -> Varchar
          time : Qtype -> Bigint
          type : Qtype -> Varchar
          channel : Qtype -> Varchar
  }")

(def qs-05)
{::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForConstantChannelJoin2"
 ::doc "
 Same as query4 but no projection of column from joined table.
 "
 ::sql "
   select s.name, ce.cot_type, ce.servertime
   from source as s
     join cot_event as ce on s.id = ce.source_id
   where s.channel = 5
      or ce.cot_type = 'a-n-A-C-F-s'
 "
 ::source-alt "
   query Qm_05 = literal : S -> S5m {
     entity
       Qchan -> {
         from
           s : source
           ce : cot_event
         where
           s = ce.source_id
           s.channel = \"3\"
         attributes
           name -> s.name
           type -> ce.cot_type
           channel -> s.channel
           time -> ce.servertime
         }
     entity
      Qtype -> {
        from
          s : source
          ce : cot_event
        where
          s = ce.source_id
          ce.cot_type = \"a-n-A-C-F-m\"
        attributes
        name -> s.name
        type -> ce.cot_type
        channel -> s.channel
        time -> ce.servertime
        }
   }
 "
 ::source "
   query Qs_05 = simple : S {
     from
       s : source
       ce : cot_event
     where
        s = ce.source_id
        OrBool(EqualVc(s.channel,\"3\"),
               EqualVc(ce.cot_type,\"a-n-A-C-F-m\")) = true
     attributes
       channel -> s.channel
       name -> s.name
       time -> ce.servertime
       type -> ce.cot_type
   }
 "
 ::target-alt "
 query Qn_05 = [ Qx ; Qm_05 ]
 "
 ::target "
 query Qt_05 = [ Qx ; Qs_05 ]
 "}

(def qs-06
  {::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForConstantMixedJoin"
   ::doc "
   Same as query5 except join across tables.
   "
   ::sql "
   select s.name, ce.cot_type, ce.servertime
   from source as s
     join cot_event as ce on s.id = ce.source_id
   where  s.channel = 5
     or ce.cot_type = 'a-n-A-C-F-m'
   "
   ::source "
   query Qs_06s = simple : S {
     from
       ce : cot_event
       s : source
     where
       s = ce.source_id
       OrBool(EqualVc(s.channel,\"3\"),
              EqualVc(ce.cot_type,\"a-n-A-C-F-m\")) = true
     attributes
       name -> s.name
       cot_type -> ce.cot_type
       time -> ce.servertime
   }
   "
   ::target "
   query Qt_06s = [ Qx ; Qs_06s ]
   "})

(def qs-07)
{::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsOnChannelInRegion"
 ::doc "
 More complex join and filter
 "
 ::sql "
 select s.name, ce.id, ce.cot_type, ce.servertime
 from source as s
   join cot_event as ce on s.id = ce.source_id
   join cot_position cep on ce.id = cep.has_cot_event
 where  s.channel = 3 and cep.tileX = 18830 and cep.tileY = 25704
 "
 ::source "
 query Qs_07s = simple : S {
   from
     ce : cot_event
     cep : cot_position
     s : source
   where
     s = ce.source_id
     ce = cep.has_cot_event
     s.channel = 3
     cep.tileX = 18830
     cep.tileY = 25704
   attributes
     name -> s.name
     cot_type -> ce.cot_type
     time -> ce.servertime
   }
 "
 ::target "
 query Qt_07s = [ Qx ; Qs_07s ]
 "}

(def sc-08 "
  schema S8 = literal : sql1 {
     entities
           Q
       attributes
           name : Q -> Varchar
           time : Q -> Bigint
           tileX : Q -> Integer
           tileY : Q -> Integer
   }
  ")

(def qs-08)
{::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForUidAndInterval"
 ::doc "
 Simple parameterized query.
 "
 ::sql-pre "
 select s.id, s.name, ce.servertime, cep.tileX, cep.tileY
 from source as s
   join cot_event as ce on s.id = ce.source_id
 where s.name = ? and ce.servertime = ?
 "
 ::sql "
 "
 ::source-pre "
 query Qs_08pre = literal : S -> S8 {
    params
       name_parm : Varchar
       servertime_parm : Bigint
    entity
      Q -> {
        from
          s : source
          ce : cot_event
          cep : cot_position
        where
          s = ce.source_id
          ce = cep.has_cot_event
          s.name = name_parm
          ce.servertime = servertime_parm
        attributes
          name -> s.name
          time -> ce.servertime
          tileX -> cep.tileX
          tileY -> cep.tileY
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
 ::target-pre "query Qt_08pre = [ Qx ; Qs_08pre ]"
 ::target "query Qt_08 = [ Qx] ; Qs_08 ]"}

(def sc-09 "
  schema S9 = literal : sql1 {
     entities
           Q
       attributes
           name : Q -> Varchar
           time : Q -> Bigint
           tileX : Q -> Integer
           tileY : Q -> Integer
     }
")

(def qs-09
  {::qname "mil.darpa.immortals.dfus.TakServerDataManager.DFU.CotEventsForUidAndIntervalBound"
   ::doc "
   Compare to query 8 except trained using bound parameters.
   Effectively treating it as a canned query but
   attributesing all results from sample parameter binding.
   "
   ::sql-pre "
   select s.id, s.name, ce.servertime, cep.tileX, cep.tileY
   from source as s
     join cot_event as ce on s.id = ce.source_id
     join cot_position cep on ce.id = cep.has_cot_event
   where s.name = ? and ce.servertime = ?
   "
   ::sql "
   where s.name = 'ABD19E' and servertime = 1494174900
   "
   ::source-pre "
   query Qs_09pre = literal : S -> S9 {
     params
        name_param : Varchar
        servertime_param : Bigint
      entity
        Q -> {
          from
            s : source
            ce : cot_event
            cep : cot_position
          where
            s = ce.source_id
            ce = cep.has_cot_event
            s.name = name_param
            ce.servertime = servertime_param
          attributes
            name -> s.name
            time -> ce.servertime
            tileX -> cep.tileX
            tileY -> cep.tileY
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
   ::target-pre "
   query Qt_09pre = [ Qx ; Qs_09pre ]
   "
   ::target "
   query Qt_09 = [ Qx ; Qs_09 ]
   "})
