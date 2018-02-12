
(require
 '(clojure.data [json :as json])
 '(clojure.tools [logging :as log])
 '(clojure [pprint :as pp]
           [string :as st])
 '(com.rpl [specter :as sr])
 '(aql.brass [data :as brass-data])
 '(aql [wrap :as aql-wrap]
       [util :as aql-util]))
;; working with the p2 cp1 brass demo
;; (require '[aql.serialize :as ser] :reload)

(import
 '(catdata.aql.exp
   AqlEnv
   AqlParser
   AqlMultiDriver)
 '(catdata LineException))
(def aql (slurp "brass_data.aql"))

(def gen (aql-wrap/generate aql))
(def env (sr/select-one [:env] gen))
(def reqs (merge brass-data/query-demo-attributes))
(aql-wrap/xform-result (merge brass-data/query-demo-attributes) gen)
(aql-util/log-info-echo "result " json/write-str)
  ;(catch Exception ex
  ;  (log/error "aql fault " ex)
  ;  (->>
  ;   {:status "aql-error"
  ;    :msg (.getMessage ex)
  ;   json/write-str))
