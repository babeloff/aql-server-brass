
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(require '[clojure.spec.alpha :as s])

(require '[aql.brass.spec :as brass-spec] :reload)
(require '[aql.brass.data :as brass-data] :reload)
(require '[aql.brass.util :as brass-util] :reload)
(require '[aql.util :as aql-util] :reload)

(def schema-perturbation
  (brass-util/convert-perturbation brass-data/sample-submission-json))
(s/explain ::brass-spec/schema-perturbation schema-perturbation)
(pp/pprint schema-perturbation)

(s/explain? ::brass-spec/schema brass-data/sc-s)

(def factory (brass-util/aql-factory
              brass-data/sc-s
              schema-perturbation))

(defn pp-identity
  ([val] (pp/pprint val) val)
  ([alt val] (do (pp/pprint [val alt])) alt))

(->> brass-data/sc-s
     pp-identity
     aql-util/serialize-aql-schema
     print)

(->> factory
     (sr/select-one [:y])
     (pp-identity brass-data/sc-y)
     aql-util/serialize-aql-schema
     print)

(->> factory
     (sr/select-one [:t])
     (pp-identity brass-data/sc-t)
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
