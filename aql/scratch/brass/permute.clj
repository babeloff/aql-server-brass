
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(require '[clojure.spec.alpha :as s])
(require '(clojure.tools [logging :as log]))

(require '[aql.spec :as aql-spec] :reload)
(require '[aql.util :as aql-util] :reload)
(require '[aql.brass.spec :as brass-spec] :reload)
(require '[aql.brass.data :as brass-data] :reload)
(require '[aql.brass.cospan :as brass-cospan] :reload)
(require '[aql.serialize :as aql-serial] :reload)

(def perturb (brass-cospan/convert-perturbation brass-data/sample-submission-json))
(s/explain ::brass-spec/schema-perturbation perturb)
;; (pp/pprint perturb)

; (pp/pprint brass-data/schema-s)
(def ent-lookup (brass-cospan/schema->col-lookup<-name brass-data/schema-s))
; (pp/pprint ent-lookup)
(def perturb-lookup (brass-cospan/perturb->col-lookup<-name perturb))
; (pp/pprint perturb-lookup)
(def col-lookup (merge-with #(conj %1 [::pert %2]) ent-lookup perturb-lookup))
; (pp/pprint col-lookup)
; (def al (brass-cospan/filter<-type ::aql-spec/attributes col-lookup))
; (pp/pprint al)
(def rl (brass-cospan/filter<-type ::aql-spec/references col-lookup))
(pp/pprint rl)

(s/explain ::aql-spec/schema brass-data/schema-s)
(s/explain ::aql-spec/schema brass-data/schema-x)
(s/explain ::aql-spec/schema brass-data/schema-t)
;; (pp/pprint brass-data/schema-x)

(s/explain ::aql-spec/mapping brass-data/mapping-s->x)
(s/explain ::aql-spec/mapping brass-data/mapping-t->x)
;; (pp/pprint brass-data/mapping-s->x)

(def factory (brass-cospan/factory
              {::brass-spec/s brass-data/schema-s
               ::brass-spec/x brass-data/schema-x
               ::brass-spec/f brass-data/mapping-s->x
               ::brass-spec/schema-perturbation perturb}))

(->> factory
     ::brass-cospan/s
     aql-util/pp-echo
     aql-serial/to-aql
     print)

(->> factory
     ::brass-cospan/x
     aql-util/pp-echo
     aql-serial/to-aql
     print)

(->> factory
     ::brass-cospan/f
     aql-util/pp-echo
     aql-serial/to-aql
     print)

(->> factory
     ::brass-cospan/t
     aql-util/pp-echo
     aql-serial/to-aql
     print)

(->> factory
     ::brass-cospan/g
     aql-util/pp-echo
     aql-serial/to-aql
     print)

(in-ns 'aql.serialize)