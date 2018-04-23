(ns aql.brass.handler
  (:require
   (com.rpl [specter :as sr])
   (aql [handler :as handler])
   (aql.brass [topics :as topics])))

(defmethod handler/aql "brass/p2/c1/json" [request]
  (if-let [action (sr/select-one ["payload"] request)]
    (topics/brass-p2c1 action)))
