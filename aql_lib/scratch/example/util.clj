
;; (require (clojure.spec [alpha :as s]))
(require '[clojure.walk :as w] :reload)
;; (require '[clojure.pprint :as pp] :reload)
(require '[clojure.string :as st] :reload)
(require '[aql.serialize :as ser] :reload)
;; (require '[aql.spec :as aql-spec] :reload)

(def cage
  [["foo" "bar" "a" "abc"]
   [::ser/equal
    ["cum-age" "e"]
    ["plus" ["age" "e"] ["age" ["manager" ["admin" "e"] "e"]]]]])

(def bindings (first cage))
(def equation (last (last cage)))

(w/walk identity identity equation)
;=> ["plus" ["age" "e"] ["age" ["manager" ["admin" "e"] "e"]]]

(ser/to-expr equation)
;=>  plus(age(e),age(manager(admin(e),e)))

(ser/forall cage)
;=> "forall foo:bar a:abc . plus(age(e),age(manager(admin(e),e)))"
