(ns aql.util
  (:require (clojure.spec [alpha :as s])))

(def echo-doc
    "For use in the ->> operator to produce
     some side effects.
     (->> :foo (echo log/info \"hello\") print)
     expands to
     (->> :foo (fn [step] (do (log/info \"hello\" step) step) print)")

(defmacro echo [action & args]
  `(fn [tru#] (do (~action ~@args tru#) tru#)))

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

(s/fdef def+
        :args (s/and vector? (comp even? count))
        :ret nil?)

(defn spiral
  ([n step coll]
   (spiral n step coll coll))
  ([n step pad coll]
   (lazy-seq
     (when-let [s (seq coll)]
       (let [p (doall (take n s))
             item (take n (apply concat p (repeat pad)))]
         (if (< 1 (count p))
           (cons item (spiral n step pad (nthrest s step)))
           (list item)))))))

(defn deep-merge* [& maps]
  (let [f (fn [old new]
             (if (and (map? old) (map? new))
                 (merge-with deep-merge* old new)
                 new))]
    (if (every? map? maps)
      (apply merge-with f maps)
     (last maps))))

(defn deep-merge [& maps]
  (let [maps (filter identity maps)]
    (assert (every? map? maps))
   (apply merge-with deep-merge* maps)))
