(ns aql.server
  (:require
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
    [util :as brass-util])
   (aql [util :as aql-util])))

(defn usage [req]
  (log/info "usage:" (keys req))
  (str "<body>
        <h1>usage</h1>
        <p>AQL processing</p>
        </body>"))

(defn async-handler [request]
  (log/info "async-handler:" (keys request))
  (svr/with-channel request channel
                    (svr/on-close channel
                                  (fn [status]
                                    (log/info "channel closed")))

                    (if (svr/websocket? channel)
                      (log/info "websocket channel")
                      (log/info "http channel"))

                    (svr/on-receive channel
                                    (fn [data]
                                      (log/info "receiving " data)
                                      (svr/send! channel data)

                                      (defn usage [req]
                                        (str "<h1>how to request AQL processing</h1>"))))))

(defn empty-handler [request]
  (log/info "empty-handler:" (keys request))
  (str "<h1>default aql handler</h1>"))

(defn aql-handler [request]
  (log/info "aql-handler")
  (if-let [action (sr/select-one [:body] request)]
    (let [model (sr/select-one ["model"] action)
          aql-env (aql-util/make-env (str model))
          return (sr/select-one ["return"] action)]
      (log/info "aql-handler:" return)
      (->> aql-env
           (aql-util/extract-result return)
           json/write-str))))

(defn brass-p2c1-handler [request]
  (if-let [action (sr/select-one [:body] request)]
    (let [pm (get action :permutation)
          base brass-data/schema-s
          factory (brass-util/aql-factory base pm)
          model [brass-data/ts-sql1
                 (->> brass-data/schema-s
                      aql-util/serialize-aql-schema)
                 (->> factory
                      (sr/select-one [:x])
                      aql-util/serialize-aql-schema)
                 (->> factory
                      (sr/select-one [:t])
                      aql-util/serialize-aql-schema)
                 (->> brass-data/mapping-x->s
                      aql-util/serialize-aql-mapping)
                 (->> brass-data/mapping-x->t
                      aql-util/serialize-aql-mapping)
                 brass-data/q1x0]
          cmd (->> (into model brass-data/query-demo) (st/join "\n"))]
      (log/info "brass phase 2 demo: " cmd)
      (spit "brass_data.aql" cmd)
      (->> cmd
           aql-util/make-env
           (aql-util/extract-result brass-data/query-demo-attributes)
           json/write-str))))

;; https://weavejester.github.io/compojure/compojure.core.html#var-routes
(http/defroutes all-routes
                (http/ANY "/" [] empty-handler)
                (http/ANY "/ws" [] async-handler)
                (http/context "/aql" []
                              (http/ANY "/json" []
                                        (-> aql-handler
                                            middleware/wrap-json-response
                                            middleware/wrap-json-body)))
                (http/context "/brass" []
                              (http/context "/p2" []
                                            (http/context "/c1" []
                                                          (http/ANY "/html" []
                                                                    brass-p2c1-handler)
                                                          (http/ANY "/json" []
                                                                    (-> brass-p2c1-handler
                                                                        middleware/wrap-json-response
                                                                        middleware/wrap-json-body)))))
                (route/not-found usage))

(def PORT 9090)
(defn -main [& args]
  (svr/run-server (hdlr/site #'all-routes) {:port PORT})
  (log/info "server started. http://127.0.0.1:" PORT))
