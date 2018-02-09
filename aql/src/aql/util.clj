(ns aql.util
  (:require
   (clojure [pprint :as pp])
   (clojure.tools [logging :as log])))

(defn echo-args [fun & args]
  (apply fun args)
  args)

;(defmacro echo [action & args]
;  "for use in the ->> operator to produce some side-effect
;   (->> :foo (echo log/info \"hello\") print)
;  Expands to
;   (->> :foo (fn [arg] (log/info \"hello\" arg) arg)  print)
;  "
;  (let [asfun (apply list 'action args)]
;    `(fn [arg] (apply ~asfun ~args) (last ~args)))

(defn log-info-echo
  ([val] (log/info val) val)
  ([name val] (log/info name val) val))

(defn pp-echo
  ([val] (pp/pprint val) val)
  ([alt val] (pp/pprint [val alt]) alt))
