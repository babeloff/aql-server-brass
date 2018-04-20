(ns aql.brass.routes
  (:require
   (aql [routes :as aql-routes])
   (org.httpkit [server :as svr])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])
   (compojure
    [route :as route]
    [handler :as hdlr]
    [core :as http])
   (ring.middleware [json :as middleware])
   (ring.util [response :refer [response]])
   (aql [util :as aql-util]
        [wrap :as aql-wrap]
        [serialize :as aql-serial])
   (aql.brass [topics :as topics])
   (aql.brass.spec [mutant :as brass-spec])))

(defn brass-p2c1-handler [request]
  (log/info "brass-p2c1-handler" request)
  (if-let [action (sr/select-one [:body] request)]
    (let [p-json (get action "permutation")]
      (log/debug "payload " p-json)
      (topics/brass-p2c1 p-json))))

;; https://weavejester.github.io/compojure/compojure.core.html#var-routes
(def brass-handlers
  [(http/context
    "/brass" []
    (http/context
     "/p2" []
     (http/context
      "/c1" []
      (http/ANY
       "/html" []
       brass-p2c1-handler)
      (http/ANY
       "/json" []
       (-> brass-p2c1-handler
           middleware/wrap-json-response
           middleware/wrap-json-body)))))])

(def brass-routes
  (apply http/routes
         (into [] cat [aql-routes/aql-handlers
                       brass-handlers
                       aql-routes/aql-fail-handlers])))
