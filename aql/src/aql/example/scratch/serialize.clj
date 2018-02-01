
(require '(com.rpl [specter :as sr]))
(require '[aql.example.data-cospan :as data] :reload)
(require '[aql.serialize :as aql-serial] :reload)
; (require '[aql.spec :as asp] :reload)


(defn to-aql [key]
  (->> key
       aql-serial/to-aql
       print))

; (to-aql data/mapping-f)
; (::asp/entity-map data/mapping-f)

(to-aql data/schema-s)
(to-aql data/schema-t)
(to-aql data/schema-r)
(to-aql data/schema-x)
(to-aql data/mapping-f)
(to-aql data/mapping-g)
(to-aql data/instance-js)
(to-aql data/instance-jx)
(to-aql data/instance-jy)
(to-aql data/instance-jx-goal)
(to-aql data/instance-jt-goal)
(to-aql data/instance-js-rt)
(to-aql data/query-qs-a)
(to-aql data/instance-ks-a)
(to-aql data/query-qs-b)
(to-aql data/instance-ks-b)
(to-aql data/query-qt-b)
(to-aql data/instance-kt-b)
(to-aql data/schema-r2)
(to-aql data/query-qs-1)
(to-aql data/instance-ks-1)
(to-aql data/query-qs-2pre)
(to-aql data/query-qs-2)
(to-aql data/instance-kx-2)
