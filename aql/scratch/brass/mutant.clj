
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(require '[clojure.spec.alpha :as s])
(require '(clojure.tools [logging :as log]))

(require '[aql.spec :as aql-spec] :reload)
(require '[aql.util :as aql-util] :reload)
(require '[aql.brass.spec.mutant :as brass-spec] :reload)
(require '[aql.brass.data :as brass-data] :reload)
(require '[aql.brass.cospan :as brass-cospan] :reload)
(require '[aql.brass.mutant :as brass-mutant] :reload)
(require '[aql.serialize :as aql-serial] :reload)
(require '[aql.brass.client :as brass-client] :reload)

(def mutant (brass-mutant/normalize brass-client/mutant-json))
(s/explain ::brass-spec/mutant mutant)
;; (pp/pprint mutant)

; (pp/pprint brass-data/schema-s)
(def ent-lookup (brass-cospan/schema->col-lookup<-name brass-data/schema-x))
; (pp/pprint ent-lookup)
(def mutant-lookup (brass-cospan/mutant->col-lookup<-name mutant))
; (pp/pprint mutant-lookup)
(def col-lookup (merge-with #(conj %1 [::pert %2]) ent-lookup mutant-lookup))
; (pp/pprint col-lookup)
(def attr-lookup (brass-cospan/filter<-type ::aql-spec/attributes col-lookup))
; (pp/pprint al)
; (def refr-lookup (brass-cospan/filter<-type ::aql-spec/references col-lookup))
(pp/pprint attr-lookup)

(def target-ent->col-lookup
  (->> mutant
     (sr/select [::brass-spec/tables sr/ALL])
     (map (fn [{name ::aql-spec/name, cols ::brass-spec/columns}]
            [name cols]))
     (into {})))
(def ent-t
  (->> mutant-lookup
       (sr/select [sr/MAP-VALS sr/LAST])
       distinct))

(def references [["source_id" "cot_action" "source"]
                 ["has_cot_action" "cot_detail" "cot_action"]
                 ["has_cot_detail" "cot_action" "cot_detail"]])

(s/explain ::aql-spec/schema brass-data/schema-s)
(s/explain ::aql-spec/schema brass-data/schema-x)
(s/explain ::aql-spec/schema brass-data/schema-t)
;; (pp/pprint brass-data/schema-x)

(s/explain ::aql-spec/mapping brass-data/mapping-f)
(s/explain ::aql-spec/mapping brass-data/mapping-g)
;; (pp/pprint brass-data/mapping-f)

(def factory (brass-cospan/factory
              {::brass-spec/s brass-data/schema-s
               ::brass-spec/x brass-data/schema-x
               ::brass-spec/f brass-data/mapping-f
               ::brass-spec/mutant mutant}))

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
