
(ns clojure.not-core
  (:require (clojure.spec [alpha :as s])))

(defmacro def+
  "binding => binding-form
  internalizes binding-forms as if by def."
  {:added "1.9", :special-form true, :forms '[(def+ [bindings*])]}
  [& bindings]
  (let [bings (partition 2 (destructure bindings))]
    (sequence cat
      ['(do)
       (map (fn [[var value]] `(def ~var ~value)) bings)
       [(mapv (fn [[var _]] (str var)) bings)]])))

(macroexpand-1 '(def+ [a b] ["a" "b"]))
(macroexpand-1 '(def+ poo "bar" [a b] ["abc" "bcd"]))

(s/fdef def+
        :args (s/and vector? (comp s/even? count))
        :ret nil?)
