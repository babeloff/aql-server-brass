
(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(require '[clojure.spec.alpha :as s])
(require '(clojure.tools [logging :as log]))
(require '[net.cgrand.xforms :as gxf])

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
(pp/pprint mutant)
(into [] brass-cospan/entity-names-xform [mutant])

(def entity-name "cot_action")
; (into (sorted-map) (brass-cospan/entity-map-attr-xform entity-name) [mutant])
; (into (sorted-map) (brass-cospan/entity-map-ref-xform entity-name) [mutant])
(pp/pprint (brass-cospan/entity-map-xform mutant entity-name))

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
