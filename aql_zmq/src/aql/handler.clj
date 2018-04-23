(ns aql.responder
  (:require
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr])
   (aql [topics :as topics])))

(defmulti aql
  (fn [request] (get request "topic")))

(defmethod aql "aql/program/eval" [request]
  (log/info "aql-handler aql/program/eval")
  (topics/aql-eval request))

(defmethod aql :default [request]
  (log/info request)
  "the aql-handler in echo mode ran: look at the responder log")
