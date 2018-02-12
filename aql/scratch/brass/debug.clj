
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
(def model (slurp "brass_data.aql"))

(def gen (aql-wrap/generate model))
(def env (sr/select-one [:env] gen))
(def reqs (merge brass-data/query-demo-attributes))
(def result (aql-wrap/xform-result reqs gen))
(aql-util/log-info-echo "result " result)

(def env-map (aql-wrap/env->maps (sr/select-one [:env] gen)))
(def queries (sr/select-one [:query] reqs))
(def query-results (map #(vector % (aql-wrap/env->query->sql env-map %)) queries))
(aql-wrap/env->query->sql env-map "Qs_01")
(::aql-wrap/query env-map)
