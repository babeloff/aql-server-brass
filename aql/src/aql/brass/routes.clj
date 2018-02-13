(ns aql.brass.routes
  (:require
   (aql [routes :as aql-routes]
        [util :as aql-util])
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
   (aql.brass
    [data :as brass-data]
    [spec :as brass-spec]
    [cospan :as brass-cospan])
   (aql [wrap :as aql-wrap]
        [serialize :as aql-serial])))

(defn brass-p2c1-handler [request]
  (log/debug "brass-p2c1-handler")
  (if-let [action (sr/select-one [:body] request)]
    (let [p-json (get action "permutation")]
      (log/debug "payload " p-json)
      (let [perturb (brass-cospan/convert-perturbation p-json)
            factory (brass-cospan/factory
                     {::brass-spec/s brass-data/schema-s
                      ::brass-spec/x brass-data/schema-x
                      ::brass-spec/f brass-data/mapping-s->x
                      ::brass-spec/schema-perturbation perturb})
            typeside [brass-data/ts-sql1
                      brass-data/ts-sql2]
            model [(->> factory
                        ::brass-cospan/s
                        aql-serial/to-aql)
                   (->> factory
                        ::brass-cospan/x
                        aql-serial/to-aql)
                   (->> factory
                        ::brass-cospan/f
                        aql-serial/to-aql)
                   (->> factory
                        ::brass-cospan/t
                        aql-serial/to-aql)
                   (->> factory
                        ::brass-cospan/g
                        aql-serial/to-aql)
                   brass-data/qgf]
            cmd (st/join "\n"
                  (sequence cat [typeside
                                 model
                                 brass-data/query-demo]))]
        (log/info "brass phase 2 demo: " cmd)
        (spit "brass_data.aql" (str cmd "\n"))
        (try
          (let [gen (aql-wrap/generate cmd)]
            (->> gen
              (aql-wrap/xform-result brass-data/query-demo-attributes)
              (aql-util/log-info-echo "result ")
              json/write-str))
          (catch Exception ex
            (log/error "aql fault " ex)
            (->>
             {:status "aql-error"
              :msg (.getMessage ex)}
             json/write-str)))))))

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