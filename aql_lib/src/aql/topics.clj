(ns aql.topics
  (:require
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr])
   (aql [wrap :as aql-wrap])))

(def helpers
  {::aql-wrap/pk-alias-lup (fn [ks] "A0ID")
   ::aql-wrap/sort-select-fn identity})

(defn aql-eval [request]
  (if-let [model (sr/select-one ["model"] request)]
    (let [_ (spit "eval_data.aql" (str model "\n"))
          aql-env (aql-wrap/generate (str model))
          return-objs (sr/select-one ["return"] request)]
      (log/info "aql-handler results:" aql-env)
      (->> aql-env
           (aql-wrap/xform-result helpers return-objs identity)
           json/write-str))))
