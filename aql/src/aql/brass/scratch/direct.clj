

;; working with the p2 cp1 brass demo
(import '(catdata.aql AqlCmdLine))
(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))

(require '[aql.brass.data :as brass-data] :reload)
(require '[aql.brass.util :as brass] :reload)
(require '[aql.util :as util] :reload)
(require '[clojure.string :as st])
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(def pert brass-data/schema-perturbation)

(def ts-0 brass-data/sql1)

(def schema-0 (util/serialize-aql-schema brass-data/sc0))
(def schema-x (util/serialize-aql-schema brass-data/scx))
(def schema-1 (util/serialize-aql-schema brass-data/sc1))

(def map-x->0 (util/serialize-aql-mapping brass-data/mapping-x->s))
(def map-x->1 (util/serialize-aql-mapping brass-data/mapping-x->t))

(def cmd (st/join "\n"
                  [ts-0
                   schema-0 schema-x schema-1
                   map-x->0 map-x->1
                   brass-data/q1x0
                   brass-data/qs01 brass-data/qs01t
                   brass-data/qs02 brass-data/qs02t]))
(spit "brass_data.aql" cmd)

(def parser (AqlParser/getParser))
(def program (.parseProgram parser cmd))
(def driver (AqlMultiDriver. program (make-array String 1)  nil))
(.start driver)
(def env (.env driver))
(def em (util/env->maps env))

(sr/select [:schema sr/MAP-KEYS] em)
(->> em
     (sr/select-one [:schema "S"])
     util/schema->sql
     print)

(sr/select [:query sr/MAP-KEYS] em)
(->> em
     (sr/select-one [:query "Qx"])
     util/query->sql
     print)

(def ent-map (brass/schema-map-by-name brass-data/sc0))
(def arrows (brass/expand-perturbation brass-data/schema-perturbation))
(def col-map (merge-with #(conj %1 [:move %2]) ent-map arrows))
(pp/pprint col-map)
(def ent-x (->> arrows (sr/select [sr/MAP-VALS]) distinct))
(brass/schema-map-by-name brass-data/sc0)
(brass/make-central-schema brass-data/sc0 brass-data/schema-perturbation)
