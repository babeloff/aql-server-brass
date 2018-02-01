
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(require '[clojure.spec.alpha :as s])

(require '[aql.spec :as aql-spec] :reload)
(require '[aql.brass.spec :as brass-spec] :reload)
(require '[aql.brass.data :as brass-data] :reload)
(require '[aql.brass.util :as brass-util] :reload)
(require '[aql.serialize :as aql-serial] :reload)

(def schema-perturbation
  (brass-util/convert-perturbation brass-data/sample-submission-json))
(s/explain ::brass-spec/schema-perturbation schema-perturbation)
;; (pp/pprint schema-perturbation)

(s/explain ::aql-spec/schema brass-data/schema-s)
(s/explain ::aql-spec/schema brass-data/schema-x)
(s/explain ::aql-spec/schema brass-data/schema-t)
;; (pp/pprint brass-data/schema-x)

(s/explain ::aql-spec/mapping brass-data/mapping-s->x)
(s/explain ::aql-spec/mapping brass-data/mapping-t->x)
;; (pp/pprint brass-data/mapping-s->x)

(def factory (brass-util/aql-cospan-factory
              brass-data/schema-s
              schema-perturbation))

(defn pp-identity
  ([val] (pp/pprint val) val)
  ([alt val] (do (pp/pprint [val alt])) alt))

(->> factory
     ::brass-util/s
     pp-identity
     aql-serial/to-aql
     print)

(->> factory
     ::brass-util/x
     (pp-identity brass-data/schema-x)
     aql-serial/to-aql
     print)

(->> factory
     ::brass-util/t
     (pp-identity brass-data/schema-t)
     aql-serial/to-aql
     print)

(->> factory
     ::brass-util/f
     (pp-identity brass-data/mapping-s->x)
     aql-serial/to-aql
     print)

(->> factory
     ::brass-util/g
     (pp-identity brass-data/mapping-t->x)
     aql-serial/to-aql
     print)
