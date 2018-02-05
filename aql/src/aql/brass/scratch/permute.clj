
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
              {::brass-spec/s brass-data/schema-s
               ::brass-spec/x brass-data/schema-x
               ::brass-spec/f brass-data/mapping-s->x
               ::brass-spec/schema-perturbation schema-perturbation}))

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
     pp-identity
     aql-serial/to-aql
     print)

(->> factory
     ::brass-util/f
     pp-identity
     aql-serial/to-aql
     print)

(->> factory
     ::brass-util/t
     pp-identity
     aql-serial/to-aql
     print)

(require '[aql.brass.util :as brass-util] :reload)
(def factory (brass-util/aql-cospan-factory
              {::brass-spec/s brass-data/schema-s
               ::brass-spec/x brass-data/schema-x
               ::brass-spec/f brass-data/mapping-s->x
               ::brass-spec/schema-perturbation schema-perturbation}))
; (def base brass-data/schema-s)
; (pp/pprint base)
; (def ent-lookup (brass-util/schema->col-lookup<-name base))
; (pp/pprint ent-lookup)
; (pp/pprint schema-perturbation)
; (def perturb-lookup (brass-util/perturb->col-lookup<-name schema-perturbation))
; (pp/pprint perturb-lookup)
; (def col-lookup (merge-with #(conj %1 [::pert %2]) ent-lookup perturb-lookup))
; (pp/pprint col-lookup)
; (def al (brass-util/filter<-type ::aql-spec/attributes col-lookup))
; (pp/pprint al)
; (def rl (brass-util/filter<-type ::aql-spec/references col-lookup))
; (pp/pprint rl)

(->> factory
     ::brass-util/g
     pp-identity
     aql-serial/to-aql
     print)
