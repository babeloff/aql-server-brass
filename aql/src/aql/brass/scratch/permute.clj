
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))

(require '[aql.brass.data :as brass-data] :reload)
(require '[aql.brass.util :as brass-util] :reload)
(require '[aql.util :as aql-util] :reload)

(def factory (brass-util/aql-factory brass-data/sc0 brass-data/schema-perturbation))

(defn pp-identity
  ([val] (pp/pprint val) val)
  ([alt val] (do (pp/pprint [val alt])) alt))

(->> brass-data/sc0
     pp-identity
     aql-util/serialize-aql-schema
     print)

(->> factory
     (sr/select-one [:x])
     (pp-identity brass-data/scx)
     aql-util/serialize-aql-schema
     print)

(->> factory
     (sr/select-one [:t])
     (pp-identity brass-data/sc1)
     aql-util/serialize-aql-schema
     print)

(->> factory
     (sr/select-one [:f])
     (pp-identity brass-data/mapping-x->s)
     aql-util/serialize-aql-mapping
     print)

(->> factory
     (sr/select-one [:g])
     (pp-identity brass-data/mapping-x->t)
     aql-util/serialize-aql-mapping
     print)
