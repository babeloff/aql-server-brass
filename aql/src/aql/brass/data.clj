;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.brass.data)

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   database/server/baseline_schema_ddl.sql

(def sc-s
  {:name "S"
   :type :schema
   :extend "sql1"
   :entities
   #{"source" "cot_event" "cot_position"}
   :attributes
   {"name" ["source" "Varchar"]
    "channel" ["source" "Varchar"]

    "cot_type"  ["cot_event" "Varchar"]
    "how"  ["cot_event" "Varchar"]
    "detail"  ["cot_event" "Text"]
    "servertime"  ["cot_event" "Bigint"]

    "point_hae" ["cot_position" "Integer"]
    "point_ce" ["cot_position" "Integer"]
    "point_le" ["cot_position" "Integer"]
    "tileX" ["cot_position" "Integer"]
    "tileY" ["cot_position" "Integer"]
    "latitude" ["cot_position" "Real"]
    "longitude" ["cot_position" "Real"]}
   :references
   {"has_source" ["cot_event" "source"]
    "has_cot_event" ["cot_position" "cot_event"]}})

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"
;;
;; The schema-mapping is supplied and the...
;;  X, F, T, and G
;; ...should be derived from it.
;;
(def schema-perturbation
  {:tables
   [{:name "cot_action"
     :columns
     [["source" "name"]
      ["source" "channel"]
      ["cot_event" "how"]
      ["cot_event" "servertime"]
      ["cot_position" "point_ce"]
      ["cot_position" "point_le"]
      ["cot_position" "tileX"]
      ["cot_position" "longitude"]
      ["cot_position" "latitude"]]}
    {:name "cot_detail"
     :columns
     [["cot_position" "point_hae"]
      ["cot_event" "detail"]
      ["cot_position" "tileY"]
      ["cot_event" "cot_type"]]}]})

(def sc-x
  {:name "X"
   :type :schema
   :extend "sql1"
   :entities
   #{["source" "cot_action"]
     ["cot_event" "cot_action"]
     ["cot_event" "cot_detail"]
     ["cot_position" "cot_action"]
     ["cot_position" "cot_detail"]}
   :attributes
   {"name" [["source" "cot_action"] "Varchar"]
    "channel" [["source" "cot_action"] "Varchar"]

    "how"  [["cot_event" "cot_action"] "Varchar"]
    "servertime"  [["cot_event" "cot_action"] "Bigint"]

    "detail"  [["cot_event" "cot_detail"] "Text"]
    "cot_type"  [["cot_event" "cot_detail"] "Varchar"]

    "point_ce" [["cot_position" "cot_action"] "Integer"]
    "point_le" [["cot_position" "cot_action"] "Integer"]
    "tileX" [["cot_position" "cot_action"] "Integer"]
    "latitude" [["cot_position" "cot_action"] "Real"]
    "longitude" [["cot_position" "cot_action"] "Real"]

    "tileY" [["cot_position" "cot_detail"] "Integer"]
    "point_hae" [["cot_position" "cot_detail"] "Integer"]}
   :references
   {"has_source"
    [["cot_event" "cot_action"]
     ["source" "cot_action"]]

    "cot_event_idx"
    [["cot_event" "cot_detail"]
     ["cot_event" "cot_action"]]
    "cot_event_idy"
    [["cot_event" "cot_action"]
     ["cot_event" "cot_detail"]]

    "has_cot_event"
    [["cot_position" "cot_action"]
     ["cot_event" "cot_detail"]]

    "cot_position_idx"
    [["cot_position" "cot_detail"]
     ["cot_position" "cot_action"]]
    "cot_position_idy"
    [["cot_position" "cot_action"]
     ["cot_position" "cot_detail"]]}})

(def mapping-x->s
  "A mapping between schema
    "
  {:name "F"
   :type :mapping
   :schemas ["X" "S"]
   :entities
   {[["source" "cot_action"] ["source"]]
    {:attributes
     {"name" "name"
      "channel" "channel"}}
    [["cot_event" "cot_action"] ["cot_event"]]
    {:references
     {"has_source" "has_source"
      "cot_event_idy" nil}
     :attributes
     {"how" "how"
      "servertime" "servertime"}}
    [["cot_event" "cot_detail"] ["cot_event"]]
    {:references
     {"cot_event_idx" nil}
     :attributes
     {"detail" "detail"
      "cot_type" "cot_type"}}
    [["cot_position" "cot_action"] ["cot_position"]]
    {:references
     {"has_cot_event" "has_cot_event"
      "cot_position_idy" nil}
     :attributes
     {"point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "latitude" "latitude"
      "longitude" "longitude"}}
    [["cot_position" "cot_detail"] ["cot_position"]]
    {:references
     {"cot_position_idx" nil}
     :attributes
     {"tileY" "tileY"
      "point_hae" "point_hae"}}}})

(def sc-y
  {:name "Y"
   :type :schema
   :extend "sql1"
   :entities
   #{"cot_cospan"}
   :attributes
   {"name" ["cot_cospan" "Varchar"]
    "channel" ["cot_cospan" "Varchar"]

    "cot_type"  ["cot_cospan" "Varchar"]
    "how"  ["cot_cospan" "Varchar"]
    "detail"  ["cot_cospan" "Text"]
    "servertime"  ["cot_cospan" "Bigint"]

    "point_hae" ["cot_cospan" "Integer"]
    "point_ce" ["cot_cospan" "Integer"]
    "point_le" ["cot_cospan" "Integer"]
    "tileX" ["cot_cospan" "Integer"]
    "tileY" ["cot_cospan" "Integer"]
    "latitude" ["cot_cospan" "Real"]
    "longitude" ["cot_cospan" "Real"]}})

(def mapping-s->y
  "A mapping between schema"
  {:name "coF"
   :type :mapping
   :schemas ["S" "Y"]
   :entities
   {[["source"] ["cot_cospan"]]
    {:attributes
     {"name" "name"
      "channel" "channel"}}

    [["cot_event"] ["cot_cospan"]]
    {:references {"has_source" nil}
     :attributes
     {"cot_type" "cot_type"
      "how" "how"
      "detail" "detail"
      "servertime" "servertime"}}

    [["cot_position"] ["cot_cospan"]]
    {:references {"has_cot_event" nil}
     :attributes
     {"point_hae" "point_hae"
      "point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "tileY" "tileY"
      "latitude" "latitude"
      "longitude" "longitude"}}}})

(def sc-t
  {:name "T"
   :type :schema
   :extend "sql1"
   :entities
   #{"cot_action" "cot_detail"}
   :attributes
   {"name"  ["cot_action" "Varchar"]
    "channel"  ["cot_action" "Varchar"]
    "how"  ["cot_action" "Varchar"]
    "servertime"  ["cot_action" "Bigint"]
    "point_ce" ["cot_action" "Integer"]
    "point_le" ["cot_action" "Integer"]
    "tileX" ["cot_action" "Integer"]
    "latitude" ["cot_action" "Real"]
    "longitude" ["cot_action" "Real"]

    "detail"  ["cot_detail" "Text"]
    "cot_type"  ["cot_detail" "Varchar"]
    "tileY" ["cot_detail" "Integer"]
    "point_hae" ["cot_detail" "Integer"]}
   :references
   {"cot_action_idx" ["cot_detail" "cot_action"]
    "cot_action_idy" ["cot_action" "cot_detail"]}})

(def mapping-x->t
  "A mapping between schema"
  {:name "G"
   :type :mapping
   :schemas ["X" "T"]
   :entities
   {[["source" "cot_action"] ["cot_action"]]
    {:attributes
     {"name" "name"
      "channel" "channel"}}
    [["cot_event" "cot_action"] ["cot_action"]]
    {:references
     {"cot_event_idy" "cot_action_idy"
      "has_source" nil}
     :attributes
     {"how" "how"
      "servertime" "servertime"}}
    [["cot_event" "cot_detail"] ["cot_detail"]]
    {:references
     {"cot_event_idx" "cot_action_idx"}
     :attributes
     {"detail" "detail"
      "cot_type" "cot_type"}}
    [["cot_position" "cot_action"] ["cot_action"]]
    {:references
     {"has_cot_event" "has_cot_event"
      "cot_position_idy" "cot_action_idy"}
     :attributes
     {"point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "latitude" "latitude"
      "longitude" "longitude"}}
    [["cot_position" "cot_detail"] ["cot_detail"]]
    {:references
     {"cot_position_idx" "cot_action_idx"}
     :attributes
     {"tileY" "tileY"
      "point_hae" "point_hae"}}}})

(def mapping-t->y
  "A mapping between schema"
  {:name "coG"
   :type :mapping
   :schemas ["T" "Y"]
   :entities
   {[["cot_action"] ["cot_cospan"]]
    {:references
     {"cot_action_idx" nil}
     :attributes
     {"name" "name"
      "channel" "channel"
      "how" "how"
      "servertime" "servertime"
      "point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "longitude" "longitude"
      "latitude" "latitude"}}
    [["cot_detail"] ["cot_cospan"]]
    {:references
     {"cot_action_idy" nil}
     :attributes
     {"point_hae" "point_hae"
      "detail" "detail"
      "tileY" "tileY"
      "cot_type" "cot_type"}}}})

(def ts-sql1
  "typeside sql1 = literal {
        imports sql
        java_functions
            EqualStr : String, String -> Bool = \"attributes input[0].equals(input[1])\"
            EqualVc : Varchar, Varchar -> Bool = \"attributes input[0].equals(input[1])\"
            EqualInt : Integer, Integer -> Bool = \"attributes input[0].equals(input[1])\"
        }")

(def ts-sql2
    "typeside sql2 = literal {
     java_types
       TEXT = \"java.lang.String\"
       VARCHAR = \"java.lang.String\"
       DATETIME = \"java.util.Date\"
       INTEGER = \"java.lang.Long\"
       REAL = \"java.lang.Double\"
       BLOB = \"java.util.ArrayList\"
       GEO = \"java.lang.Long\"
       BOOLEAN = \"java.lang.Boolean\"
       true = \"java.lang.Boolean\"
     java_constants
       TEXT = \"return input[0]\"
       VARCHAR = \"return input[0]\"
       DATETIME = \"return new java.util.Date(java.lang.Long.decode(input[0]))\"
       INTEGER = \"return java.lang.Long.decode(input[0])\"
       REAL = \"return java.lang.Double.parseDouble(input[0])\"
       BLOB = \"return []\"
       GEO = \"return java.lang.Long.decode(input[0])\"
       BOOLEAN = \"return new java.lang.Boolean(input[0])\"
       true = \"return true\"
     java_functions
       int_to_real : INTEGER -> REAL = \"return 0.0 + input[0]\"
       real_to_int : REAL -> INTEGER = \"return Math.round(input[0]).longValue()\"
       date_to_int : DATETIME -> INTEGER = \"return input[0].getTime()\"
       int_to_date : INTEGER -> DATETIME = \"return new java.util.Date(input[0])\"
       txt_to_vc : TEXT -> VARCHAR = \"return input[0]\"
       vc_to_txt : VARCHAR -> TEXT = \"return input[0]\"
       real_to_geo : REAL -> GEO = \"return Math.round(input[0] * 1E6).longValue()\"
       geo_to_real : GEO -> REAL = \"return input[0] / 1E6\"
       now : -> DATETIME = \"return java.util.Date.from(java.time.Instant.now())\"
       eqVc : VARCHAR, VARCHAR -> BOOLEAN = \"return input[0].equals(input[1])\"
       eqInt : INTEGER, INTEGER -> BOOLEAN = \"return input[0] == input[1]\"
       or : BOOLEAN, BOOLEAN -> BOOLEAN = \"return input[0] || input[1]\")
     }
  ")


(def q1x0 "query Qx = [ toQuery G ; toCoQuery F ]")

 ;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
 ;;  database/server/aql/src/aql/cp2_1_db.aql#L262

(def qs-01-doc "
  ## Query 1 : cot_eventsForConstantCotType
  Basic test against single table with a simple filter on a projected column.
  In the original query the source_id was extracted by the query.
  In AQL foreign-key values are meaningless so extracting them is meaningless.
  If what is intended is some meaningful key indicating a row in the source entity;
  then a uuid on the source entity is probably what is meant.

  	select ce.id, ce.source_id, ce.cot_type
  	from cot_event as ce
  	where ce.cot_type = 'a-n-A-C-F-s'
  ")
(def qs-01
  "query Qs_01 = simple : S  {
    from ce:cot_event
    where ce.cot_type = \"a-n-A-C-F-m\"
    attributes
        cot_type -> ce.cot_type
    }")

(def qt-01 "query Qt_01 = [ Qx ; Qs_01 ]")
;; instance q1_inst = eval Qs_01 S_inst
;; instance q1_inst = eval Qt_01 T_inst

(def qs-02-doc "
## Query 2 : cot_eventsForConstantTimeInterval
Like query 1 except filter on non-projected column.

	select ce.id, ce.source_id, ce.cot_type, ce.how
	from cot_event as ce
	where ce.servertime = 201705071635

")

(def qs-02
  "query Qs_02 = simple : S  {
    from ce:cot_event
    where ce.servertime = \"201705071635\"
    attributes
        cot_type -> ce.cot_type
        how -> ce.how
    }")

(def qt-02 "query Qt_02 = [ Qx ; Qs_02 ]")
;; instance q2_inst = eval Qs_02 S_inst
;; instance q2_inst = eval Qt_02 T_inst

(def qs-03-doc "
## Query 3 : cot_eventsForConstantCompoundFilter
Query with a simple compound filter

	select ce.id, ce.source_id, ce.cot_type, ce.how
	from cot_event as ce
	where ce.servertime = 201705071635
	and ce.cot_type = 'a-n-A-C-F-m'
")

(def qs-03
  "query Qs_03 = simple : S {
     from ce : cot_event
     where
       ce.server_time = \"201705071635\"
       ce.cot_type = \"a-n-A-C-F-m\"
     attributes
       cot_type -> ce.cot_type
       how -> ce.how
     }")

(def qt-03 "query Qt_03 = [ Qx ; Qs_03 ]")
;; instance q3_inst = eval Qs_03 S_inst
;; instance q3_inst = eval Qt_03 T_inst

(def qs-04-doc  "
## Query 4 : cot_eventsForConstantChannelJoin
Simple join with filter

	select s.name, ce.id, ce.cot_type, ce.servertime
	from source as s
	join cot_event as ce on s.id = ce.source_id
	where s.channel = 7
")

(def qs-04
  "query Qs_04 = simple : S {
     from
       ce : cot_event
       s : source
     where
       s = ce.source_id
       s.channel = \"7\"
     attributes
       name -> s.name
       cot_type -> ce.cot_type
       time -> ce.server_time
  }")

(def qt-04 "query Qt_04 = [ Qx ; Qs_04 ]")
;; instance q4_inst = eval Qs_04 S_inst
;; instance q4_inst = eval Qt_04 T_inst

(def qs-05-doc "
## Query 5 : cot_eventsForConstantChannelJoin2
Same as query4 but no projection of column from joined table.

	select s.name, ce.cot_type, ce.servertime
	from source as s
	join cot_event as ce on s.id = ce.source_id
	where s.channel = 5 or ce.cot_type = 'a-n-A-C-F-s'
")

(def qs-05s
  "query Qs_05s = simple : S {
     from
       ce : cot_event
       s : source
     where
       s = ce.source_id
       eqInt(s.channel,3) = true
       //or(eqInt(s.channel,5), eqVc(ce.cot_type,\"a-n-A-C-F-m\")) = true
     attributes
       name -> s.name
       cot_type -> ce.cot_type
       time -> ce.server_time
  }")

(def qt-05s "query Qt_05s = [ Qx ; Qs_05s ]")
;; instance q5s_inst =
;;   eval Qs_05s S_inst
;;  ~=
;;   eval Qt_05s T_inst

(def sc-05
  "schema S5 = literal : sql2 {
    entities
          Q
      attributes
          name : Q -> VARCHAR
          time : Q -> INTEGER
          type : Q -> VARCHAR
          channel : Q -> INTEGER
  }")

(def qs-05
  "query Qs_05 = literal : S -> S5 {
     entities Q -> {
       from
         ce : cot_event
         s : source
       where
         s = ce.source_id
       attributes
         name -> s.name
         type -> ce.cot_type
         channel -> s.channel
         time -> ce.server_time
     }
    }")

(def qt-05 "query Qt_05 = [ Qx ; Qs_05 ]")
;; instance s5_inst = eval Qs_05 S_inst
;; instance s5_inst = eval Qt_05 T_inst

;; schema S5simple = dst q5c

(def qs-05a
  "query Qs_05a = literal : S5 -> S5 {
    entities
      Q -> {
        from q: Q
        where eqInt(q.channel,\"3\") = true
        attributes
          name -> q.name
          type -> q.type
          channel -> q.channel
          time -> q.time
     }
  }")

(def qt-05a "query Qt_05a = [ Qx ; Qs_05a ]")
;; instance q5a_inst = eval Qs_05a S_inst
;; instance q5a_inst = eval Qt_05a T_inst

(def qs-05b
  "query Qs_05b = literal : S5 -> S5 {
    entities
      Q -> {
        from q: Q
        where
          q.type = \"a-n-A-C-F-m\"
          eqInt(q.channel,\"3\") = false
        attributes
          name -> q.name
          type -> q.type
          channel -> q.channel
          time -> q.time
        }
      }")

(def qt-05b "query Qt_05b = [ Qx ; Qs_05b ]")
;; instance q5b_inst = eval Qt_05b s5_inst
;; instance q5c_inst = coproduct q5a_inst + q5b_inst : S5

(def qs-06-doc  "
## Query 6 : cot_eventsForConstantMixedJoin
Same as query5 except join across tables.

	select s.name, ce.cot_type, ce.servertime
	from source as s
	join cot_event as ce on s.id = ce.source_id
	where  s.channel = 5
		or ce.cot_type = 'a-n-A-C-F-m'

")

(def qs-06s
  "query Qs_06s = simple : S {
    from
      ce : cot_event
      s : source
    where
      s = ce.source_id
      or(eqInt(s.channel,\"5\"), eqVc(ce.cot_type,\"a-n-A-C-F-m\") = true
    attributes
      name -> s.name
      cot_type -> ce.cot_type
      time -> ce.server_time
  }")

(def qt-06s "query Qt_06s = [ Qx ; Qs_06s ]")
;; instance q6_inst = eval Qs_06s S_inst
;; instance q6_inst = eval Qt_06s T_inst

(def qs-07-doc "
## Query 7 : cot_eventsOnChannelInRegion
More complex join and filter

	select s.name, ce.id, ce.cot_type, ce.servertime
	from source as s
	join cot_event as ce on s.id = ce.source_id
	join cot_event_position cep on ce.id = cep.cot_event_id
	where  s.channel = 3 and cep.tilex = 18830 and cep.tiley = 25704

")

(def qs-07s
  "query Qs_07s = simple : S {
    from
      ce : cot_event
      cep : cot_event_position
      s : source
    where
      s = ce.source_id
      ce = cep.cot_event_id
      s.channel = 3
    attributes
      name -> s.name
      cot_type -> ce.cot_type
      time -> ce.server_time
    }")

; query q7a = simple : S {}
;  import q7
;  where cep.tilex = 18830 cep.tiley = 25704)

; query q7b = simple : S {}
;  import q7
;  where s.channel = 3)

(def qt-07s "query Qt_07s = [ Qx ; Qs_07s ]")
; instance q7_inst = eval Qs_07s S_inst
; instance q7_inst = eval Qt_07s T_inst

(def qs-08-doc "
## Query 8 : cot_eventsForUidAndInterval
Simple parameterized query.

	select s.id, s.name, ce.servertime, cep.tilex, cep.tiley
	from source as s
	join cot_event as ce on s.id = ce.source_id
	where s.name = ? and ce.server_time = ?
")

(def sc-08
  "schema S8 = literal : sql2 {
     entities
           Q
       attributes
           name : Q -> VARCHAR
           time : Q -> INTEGER
           tileX : Q -> GEO
           tileY : Q -> GEO
   }")

(def qs-08
  "query Qs_08 = literal : S -> S8 {
    entities
      Q -> {
        from
          s : source
          ce : cot_event
          cep : cot_event_position
        where
          s = ce.source_id
          ce = cep.cot_event_id
        attributes
          name -> s.name
          time -> ce.server_time
          tileX -> cep.tile_x
          tileY -> cep.tile_y
      }
  }")

(def qs-08p
  "query Qs_08p = simple : S8 {
    from q : Q
    where
      q.name = \"A6A7DC\"
      q.time = \"201705071635\"
    attributes
        name -> q.name
        time -> q.time
        tileX -> q.tileX
        tileY -> q.tileY
      }")

(def qt-08 "query Qt_08 = [ Qx ; Qs_08 ]")
(def qt-08p "query Qt_08p = [ Qt_08 ; Qs_08p ]")
;; instance q8a_inst = eval Qs_08p S_inst
;; instance q8a_inst = eval Qt_08p T_inst

(def parameterized-doc "

Since this is a parameterized query, we reproduce the sampling
that would be created with baseline application testing.
For a subset of source name and servertime parameters,
we take a representative sample of the query results for that value.
If we just sample over the whole population of records, we
might get just a few results for a set of parameters.

	with sampleSizes as
	(
	select source_id, servertime, min(sample_size) as sample_size
	from
		(select s.id source_id, ce.servertime,
		round((cast(count() over(partition by s.id, ce.servertime) as float) / cast( count() over() as float)) * 1000) as sample_size
		from source s join cot_event ce on s.id = ce.source_id) as t1
		group by t1.source_id, t1.servertime
		),

	samples as
	(
	select source_id, source_name, servertime, row_number() over(partition by source_id, servertime) as rownum, tilex, tiley
	from
		(select s.id as source_id, s.name as source_name, ce.servertime, cep.tilex, cep.tiley,
		row_number() over(order by s.id, ce.servertime, random()) as rownum
		from source s join cot_event ce on s.id = ce.source_id
		join cot_event_position cep on ce.id = cep.cot_event_id
		join (	select t1.id, t1.servertime
				from (select distinct S.id, ce2.servertime
				      from source S join cot_event ce2 on S.id = ce2.source_id
				     ) t1
				order by random()
				limit 50) as t2 on s.id = t2.id and ce.servertime = t2.servertime
		) as t3
	)

	select samples.source_id as id, samples.source_name as name, samples.servertime, samples.tilex, samples.tiley
	from samples join sampleSizes on samples.source_id = sampleSizes.source_id and samples.servertime = sampleSizes.servertime
	where samples.rownum <= sampleSizes.sample_size

")

(def qs-09-doc "
## Query 9 : cot_eventsForUidAndIntervalBound
Compare to query 8 except trained using bound parameters.
Effectively treating it as a canned query but
attributesing all results from sample parameter binding.

	select s.id, s.name, ce.servertime, cep.tilex, cep.tiley
	from source as s
	join cot_event as ce on s.id = ce.source_id
	join cot_event_position cep on ce.id = cep.cot_event_id
	where s.name = ? and ce.server_time = ?

Samples:

	where s.name = 'ABD19E' and servertime = 201705071645

")

(def sc-09
  "schema S9 = literal : sql2 {
     entities
           Q
       attributes
           name : Q -> VARCHAR
           time : Q -> INTEGER
           tileX : Q -> GEO
           tileY : Q -> GEO
     }")

(def qs-09
  "query Qs_09 = literal : S -> S9 {
     entities
       Q -> {
         from
           s : source
           ce : cot_event
           cep : cot_event_position
         where
           s = ce.source_id
           ce = cep.cot_event_id
         attributes
           name -> s.name
           time -> ce.server_time
           tileX -> cep.tile_x
           tileY -> cep.tile_y
         }
     }")

(def qs-09p
  "query Qs_09p = simple : S9 {
    from q : Q
    where
      q.name = \"A6A7DC\"
      q.time = \"201705071635\"
    attributes
        name -> q.name
        time -> q.time
        tileX -> q.tileX
        tileY -> q.tileY
      }")

(def qt-09 "query Qt_09 = [ Qx ; Qs_09 ]")
(def qt-09p "query Qt_09p = [ Qt_09 ; Qs_09p ]")
;; instance q9_inst = eval Qt_09p T_inst
;; instance q9_inst = eval Qt_09p T_inst

(def query-demo
  "all the queries for the demo
    Includes all the initial queries as well as the targets"
  [qs-01 qt-01
   qs-02 qt-02])
   ;qs-03 qt-03
   ;qs-04 qt-04
   ;qs-05 qt-05
   ;qs-05s qt-05s
   ;qs-05a qt-05a
   ;qs-05b qt-05b
   ;qs-06s qt-06s
   ;qs-07s qt-07s
   ;qs-08 qt-08
   ;qs-08s qt-08s
   ;qs-09 qt-09
   ;qs-09 qt-09

(def query-demo-attributes
  "a list of the queries to attributes"
  {:query ["Qt_01"
           "Qt_02"
           "Qt_03"
           "Qt_04"
           "Qt_05"
           "Qt_05s"
           "Qt_05a"
           "Qt_05b"
           "Qt_06s"
           "Qt_07s"
           "Qt_08"
           "Qt_08s"
           "Qt_09"]})
