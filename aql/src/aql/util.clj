(ns aql.util
  (:require
   (clojure [pprint :as pp])
   (clojure.tools [logging :as log])))


(defn log-info-echo
  ([val] (log/info val) val)
  ([name val] (log/info name val) val))

(defn pp-echo
  ([val] (pp/pprint val) val)
  ([alt val] (pp/pprint [val alt]) alt))
