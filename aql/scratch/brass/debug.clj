(ns scratch.brass.debug
  (:require
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (clojure [pprint :as pp]
            [string :as st])
   (aql.brass [data :as brass-data])
   (aql [wrap :as aql-wrap])))
;; working with the p2 cp1 brass demo

;; (require '[aql.serialize :as ser] :reload)
(require '[aql.brass.server :as abs] :reload)

; (dbg/break ser/aql-format)
; (dbg/break-catch ser/aql-format)

(abs/-main)

(def aql (slurp "brass_data.aql"))

(try
  (let [env (aql-wrap/make-env aql)]
    (->> env
      (aql-wrap/extract-result [brass-data/query-demo-attributes])
      (log/info "result ")
      json/write-str))
  (catch Exception ex
    (log/error "aql fault " ex)
    (->>
     {:status "aql-error"
      :msg (.getMessage ex)}
     json/write-str)))
