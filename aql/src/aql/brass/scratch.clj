

;; working with the p2 cp1 brass demo
(import '(catdata.aql AqlCmdLine))
(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))

(require '[aql.brass.data :as brass] :reload)
(require '[aql.util :as util] :reload)
(require '[clojure.string :as st])

(def ts-0 brass/sql1)

(def schema-0 (util/serialize-aql-schema brass/sc0))
(def schema-x (util/serialize-aql-schema brass/scx))
(def schema-1 (util/serialize-aql-schema brass/sc1))

(def map-x->0 (util/serialize-aql-mapping brass/mapping-sx->s0))
(def map-x->1 (util/serialize-aql-mapping brass/mapping-sx->s1))

(def query-01 brass/qs01)
(def query-02 brass/qs02)

(def cmd (st/join "\n"
            [   ts-0
                schema-0 schema-x schema-1
                map-x->0 map-x->1
                brass/q1x brass/qx0 brass/q1x0
                query-01 brass/qs01t
                query-02 brass/qs02t]))
(spit "brass_data.aql" cmd)

(def parser (AqlParser/getParser))
(def program (.parseProgram parser cmd))
(def driver (AqlMultiDriver. program (make-array String 1)  nil))
(.start driver)
(def env (.env driver))
(def em (util/env->maps env))

(def schema1 (util/env->schema env "s0"))
(util/wrap-schema schema schema1)
(util/schema->sql schema1)

(def query1 (util/env->query env "q01"))
;; (util/wrap-query query query1)
(util/query->sql query1)

