(ns aql.server 
    (:require 
        (org.httpkit [server :as svr])
        (clojure.data [json :as json])
        (clojure.tools [logging :as log])
        (compojure 
            [route :as route]
            [handler :as hdlr]
            [core :as http])
        (aql.brass [data :as brass])))

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
    (let [params (get request :query-params nil)
          action-str (get params "action")
          action (json/read-str action-str)
          model (get action "model")
          return (get action "return")]
        (log/info "aql-handler:" action)
        (str "<body>
            <h1>AQL Handler</h1>
            <h2>model</h2> <p>" 
                model
            "</p><h2>return</h2> <p>" 
            return
            "</p>
            </body>")))

(defn brass-p2c1 [request]
    (log/info "brass-handler:" (keys request)) 
    (if-let [act0 (get-in request [:params :permutation] nil)]
        (let  [act1 (json/read-str act0)]
            (str "<body>
                <h1>BRASS P2 CP1 Handler</h1>
                <p>" 
                (str act1)
                "</p>
                </body>"))
        "<body>
        <h1>brass p2c1 handler</h1>
        <p>failure</p>
        </body>"))
     
;; https://weavejester.github.io/compojure/compojure.core.html#var-routes
(http/defroutes all-routes 
    (http/ANY "/" [] empty-handler)
    (http/ANY "/ws" [] async-handler)
    (http/ANY "/aql" [] aql-handler)
    (http/context "/brass" []
        (http/ANY "/p2/c1" [] brass-p2c1))
    ;; (route/files "/static/") 
    (route/not-found usage))

(def PORT 9090)
(defn -main [& args] 
    (svr/run-server (hdlr/site #'all-routes) {:port PORT})
    (log/info "server started. http://127.0.0.1:" PORT))
