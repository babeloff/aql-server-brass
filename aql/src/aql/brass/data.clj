;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.brass.data
  (:require (aql [spec :as s])))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   database/server/baseline_schema_ddl.sql

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"

(def sample-submission-json
  {"martiServerModel"
   {"requirements"
    {"postgresqlPerturbation"
     {"tables"
      [{"table"  "cot_action"
        "columns"
         ["CotEvent_How"
          "CotEvent_ServerTime"
          "Position_PointCE"
          "Position_PointLE"
          "Position_TileX"
          "Position_Longitude"
          "Position_Latitude"]}
       {"table" "cot_detail"
        "columns"
        ["Position_PointHae"
         "CotEvent_Detail"
         "Position_TileY"
         "CotEvent_CotType"]}]}}}})


;; The schema-mapping is supplied and the...
;;  X, F, T, and G
;; ...should be derived from it.
;;
;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;   das/das-testharness-coordinator/src/main/java/mil/darpa/immortals/
;;   core/api/ll/phase2/martimodel/requirements/storage/postgresql/DatabaseColumns.java
(def schema-s
  #::s
  {:name "S"
   :type ::s/schema
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
   {"source_id" ["cot_event" "source"]
    "has_cot_event" ["cot_position" "cot_event"]}})


(def schema-x
  #::s
  {:name "X"
   :type ::s/schema
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

(def mapping-s->x
  "A mapping between schema"
  #::s
  {:name "F"
   :type ::s/mapping
   :schema-map ["S" "X"]
   :entity-map
   {[["source"] ["cot_cospan"]]
    #::s
    {:attribute-map
     {"name" "name"
      "channel" "channel"}}

    [["cot_event"] ["cot_cospan"]]
    #::s
    {:reference-map {"source_id" nil}
     :attribute-map
     {"cot_type" "cot_type"
      "how" "how"
      "detail" "detail"
      "servertime" "servertime"}}

    [["cot_position"] ["cot_cospan"]]
    #::s
    {:reference-map {"has_cot_event" nil}
     :attribute-map
     {"point_hae" "point_hae"
      "point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "tileY" "tileY"
      "latitude" "latitude"
      "longitude" "longitude"}}}})

(def schema-t
  #::s
  {:name "T"
   :type ::s/schema
   :extend "sql1"
   :entities
   #{"source" "cot_action" "cot_detail"}
   :attributes
   {"name"  ["source" "Varchar"]
    "channel"  ["source" "Varchar"]

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
   {"source_id" ["cot_action" "source"]
    "cot_action_idx" ["cot_detail" "cot_action"]
    "cot_action_idy" ["cot_action" "cot_detail"]}})


(def mapping-t->x
  "A mapping between schema"
  #::s
  {:name "G"
   :type ::s/mapping
   :schema-map ["T" "X"]
   :entity-map
   {
     [["entity source"] ["cot_cospan"]]
     {:attribute-map
       {"name" "name"
        "channel" "channel"}}
    [["cot_action"] ["cot_cospan"]]
    #::s
    {:reference-map
     {"has_cot_detail" nil
      "source_id" nil}
     :attribute-map
     {"how" "how"
      "servertime" "servertime"
      "point_ce" "point_ce"
      "point_le" "point_le"
      "tileX" "tileX"
      "longitude" "longitude"
      "latitude" "latitude"}}
    [["cot_detail"] ["cot_cospan"]]
    #::s
    {:reference-map
     {"has_cot_action" nil}
     :attribute-map
     {"point_hae" "point_hae"
      "detail" "detail"
      "tileY" "tileY"
      "cot_type" "cot_type"}}}})

(def ts-sql1
  "typeside sql1 = literal {
        imports sql
        java_types
          Geo = \"java.lang.Long\"
        java_constants
          Geo = \"return java.lang.Long.decode(input[0])\"
        java_functions
          EqualStr : String, String -> Bool = \"return input[0].equals(input[1])\"
          EqualVc : Varchar, Varchar -> Bool = \"return input[0].equals(input[1])\"
          EqualInt : Integer, Integer -> Bool = \"return input[0].equals(input[1])\"
          OrBool : Bool, Bool -> Bool = \"return input[0] || input[1]\"
        }")

(def ts-sql2
  "typeside sql2 = literal {
     imports sql
     java_types
       Geo = \"java.lang.Long\"
     java_constants
       Geo = \"return java.lang.Long.decode(input[0])\"
     java_functions
       int_to_real : Bigint -> Real = \"return 0.0 + input[0]\"
       real_to_int : Real -> Bigint = \"return Math.round(input[0]).longValue()\"
       date_to_int : Timestamp -> Bigint = \"return input[0].getTime()\"
       int_to_date : Bigint -> Timestamp = \"return new java.util.Date(input[0])\"
       txt_to_vc : Text -> Varchar = \"return input[0]\"
       vc_to_txt : Varchar -> Text = \"return input[0]\"
       real_to_geo : Real -> Geo = \"return Math.round(input[0] * 1E6).longValue()\"
       geo_to_real : Geo -> Real = \"return input[0] / 1E6\"
       now : -> Timestamp = \"return java.util.Date.from(java.time.Instant.now())\"
       eqVc : Varchar, Varchar -> Boolean = \"return input[0].equals(input[1])\"
       eqInt : Bigint, Bigint -> Boolean = \"return input[0] == input[1]\"
       // or : Boolean, Boolean -> Boolean = \"return input[0] || input[1]\")
     }
  ")

(def qgf "query Qx = [ toCoQuery G ; toQuery F ]")

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
    from
     ce:cot_event
    where
     ce.cot_type = \"a-n-A-C-F-m\"
    attributes
     // source_id -> ce.source_id
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
	where ce.servertime = 1494174900 // 2017-05-07t16:35

")

(def qs-02
  "query Qs_02 = simple : S  {
    from
     ce:cot_event
    where
     ce.servertime = \"1494174900\"
    attributes
     // source_id -> ce.source_id
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
	where ce.servertime = 1494174900
	and ce.cot_type = 'a-n-A-C-F-m'
")

(def qs-03
  "query Qs_03 = simple : S {
     from ce : cot_event
     where
       ce.servertime = \"1494174900\"
       ce.cot_type = \"a-n-A-C-F-m\"
     attributes
       // source_id -> ce.source_id
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
       time -> ce.servertime
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
	where s.channel = 5
     or ce.cot_type = 'a-n-A-C-F-s'
")

;; instance q5s_inst =
;;   eval Qs_05s S_inst
;;  ~=
;;   eval Qt_05s T_inst

(def sc-05
  "schema S5 = literal : sql1 {
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

(def qs-05
  "query Qs_05 = literal : S -> S5 {
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
  }")
(def qt-05 "query Qt_05 = [ Qx ; Qs_05 ]")

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
      OrBool(EqualVc(s.channel,\"5\"),
             EqualVc(ce.cot_type,\"a-n-A-C-F-m\")) = true
    attributes
      name -> s.name
      cot_type -> ce.cot_type
      time -> ce.servertime
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
	join cot_position cep on ce.id = cep.has_cot_event
	where  s.channel = 3 and cep.tileX = 18830 and cep.tileY = 25704

")

(def qs-07s
  "query Qs_07s = simple : S {
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
    }")

(def qt-07s "query Qt_07s = [ Qx ; Qs_07s ]")
; instance q7_inst = eval Qs_07s S_inst
; instance q7_inst = eval Qt_07s T_inst

(def qs-08-doc "
## Query 8 : cot_eventsForUidAndInterval
Simple parameterized query.

	select s.id, s.name, ce.servertime, cep.tileX, cep.tileY
	from source as s
	join cot_event as ce on s.id = ce.source_id
	where s.name = ? and ce.servertime = ?
")

(def sc-08
  "schema S8 = literal : sql1 {
     entities
           Q
       attributes
           name : Q -> Varchar
           time : Q -> Bigint
           tileX : Q -> Integer
           tileY : Q -> Integer
   }")

(def qs-08pre
  "query Qs_08pre = literal : S -> S8 {
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
    } ")

(def qs-08
  "query Qs_08 = literal : S -> S8 {
     bindings
        name_parm = \"A6A7DC\"
        servertime_parm = \"1494174900\"
     imports Qs_08pre
  }")

(def qt-08pre "// FIXME query Qt_08pre = [ Qx ; Qs_08pre ]")
(def qt-08 "// FIXME query Qt_08 = [ Qx ; Qs_08 ]")
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
	select source_id, source_name, servertime, row_number() over(partition by source_id, servertime) as rownum, tileX, tileY
	from
		(select s.id as source_id, s.name as source_name, ce.servertime, cep.tileX, cep.tileY,
		row_number() over(order by s.id, ce.servertime, random()) as rownum
		from source s join cot_event ce on s.id = ce.source_id
		join cot_position cep on ce.id = cep.has_cot_event
		join (	select t1.id, t1.servertime
				from (select distinct S.id, ce2.servertime
				      from source S join cot_event ce2 on S.id = ce2.source_id
				     ) t1
				order by random()
				limit 50) as t2 on s.id = t2.id and ce.servertime = t2.servertime
		) as t3
	)

	select samples.source_id as id, samples.source_name as name, samples.servertime, samples.tileX, samples.tileY
	from samples join sampleSizes on samples.source_id = sampleSizes.source_id and samples.servertime = sampleSizes.servertime
	where samples.rownum <= sampleSizes.sample_size

")

(def qs-09-doc "
## Query 9 : cot_eventsForUidAndIntervalBound
Compare to query 8 except trained using bound parameters.
Effectively treating it as a canned query but
attributesing all results from sample parameter binding.

	select s.id, s.name, ce.servertime, cep.tileX, cep.tileY
	from source as s
	join cot_event as ce on s.id = ce.source_id
	join cot_position cep on ce.id = cep.has_cot_event
	where s.name = ? and ce.servertime = ?

Samples:

	where s.name = 'ABD19E' and servertime = 1494174900

")

(def sc-09
  "schema S9 = literal : sql1 {
     entities
           Q
       attributes
           name : Q -> Varchar
           time : Q -> Bigint
           tileX : Q -> Integer
           tileY : Q -> Integer
     }")

(def qs-09pre
  "query Qs_09pre = literal : S -> S9 {
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
     }")

(def qs-09
  "query Qs_09 = literal : S -> S9 {
     bindings
        name_param = \"A6A7DC\"
        servertime_param = \"1494174900\"

     imports Qs_09pre
  }")

(def qt-09pre "// FIXME query Qt_09pre = [ Qx ; Qs_09pre ]")
(def qt-09 "// FIXME query Qt_09 = [ Qx ; Qs_09 ]")
;; instance q9_inst = eval Qt_09p T_inst
;; instance q9_inst = eval Qt_09p T_inst

(def query-demo
  "all the queries for the demo
    Includes all the initial queries as well as the targets"
  [qs-01 qt-01
   qs-02 qt-02
   qs-03 qt-03
   qs-04 qt-04
   sc-05
   qs-05 qt-05
   qs-06s qt-06s
   qs-07s qt-07s
   sc-08
   qs-08pre qt-08pre
   qs-08 qt-08
   sc-09
   qs-09pre qt-09pre
   qs-09 qt-09])

(def query-demo-attributes
  "a list of the queries to return"
  {:query ["Qs_01" "Qt_01"
           "Qs_02" "Qt_02"
           "Qs_03" "Qt_03"
           "Qs_04" "Qt_04"
           "Qs_05" "Qt_05"
           "Qs_06s" "Qt_06s"
           "Qs_07s" "Qt_07s"
           "Qs_08pre" "Qt_08pre"
           "Qs_08" "Qt_08"
           "Qs_09pre" "Qt_09pre"
           "Qs_09" "Qt_09"]})
