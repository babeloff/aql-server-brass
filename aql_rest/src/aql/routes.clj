(ns aql.routes
  (:require
   (org.httpkit [server :as svr])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (clojure.tools.nrepl [server :as nrs])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])
   (compojure
    [route :as route]
    [handler :as hdlr]
    [core :as http])
   (ring.middleware [json :as middleware])
   (ring.util [response :refer [response]])
   [aql.wrap :as aql-wrap]))

(defn usage [req]
  (log/info "usage:" (keys req))
  (str "<body>
        <h1>usage</h1>
        <p>AQL processing</p>
        </body>"))

(def channel "placeholder to prevent a linter error message")

(defn async-handler [request]
  (log/info "async-handler:" (keys request))
  (svr/with-channel
   request
   channel
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
          aql-env (aql-wrap/generate (str model))
          return-objs (sr/select-one ["return"] action)]
      (log/info "aql-handler:" return-objs)
      (->> aql-env
           (aql-wrap/xform-result return-objs identity)   
           json/write-str))))

;; https://weavejester.github.io/compojure/compojure.core.html#var-routes
(def aql-handlers
  [(http/ANY "/" [] empty-handler)
   (http/ANY "/ws" [] async-handler)
   (http/context
    "/aql" []
    (http/ANY
     "/json" []
     (-> aql-handler
         middleware/wrap-json-response
         middleware/wrap-json-body)))])

(def aql-fail-handlers
  [(route/not-found usage)])

(def aql-routes
  (apply http/routes
         (into [] cat [aql-handlers aql-fail-handlers])))
