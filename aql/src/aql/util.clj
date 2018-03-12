(ns aql.util)

(def echo-doc
    "For use in the ->> operator to produce
     some side effects.
     (->> :foo (echo log/info \"hello\") print)
     expands to
     (->> :foo (fn [step] (do (log/info \"hello\" step) step) print)")

(defmacro echo [action & args]
  `(fn [tru#] (do (~action ~@args tru#) tru#)))
