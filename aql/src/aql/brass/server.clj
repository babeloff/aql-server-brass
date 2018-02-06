(ns aql.brass.server
  (:require
   (aql [server :as papa])
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
    [util :as brass-util])
   (aql [wrap :as aql-wrap]
        [serial :as aql-serial])))

(defn brass-p2c1-handler [request]
  (if-let [action (sr/select-one [:body] request)]
    (let [p-json (get action :permutation)
          perturb (brass-util/convert-perturbation p-json)
          factory (brass-util/aql-cospan-factory
                   {::brass-spec/s brass-data/schema-s
                    ::brass-spec/x brass-data/schema-x
                    ::brass-spec/f brass-data/mapping-s->x
                    ::brass-spec/schema-perturbation perturb})
          model [brass-data/ts-sql1
                 (->> factory
                      ::brass-util/s
                      aql-serial/to-aql)
                 (->> factory
                      ::brass-util/x
                      aql-serial/to-aql)
                 (->> factory
                      ::brass-util/f
                      aql-serial/to-aql)
                 (->> factory
                      ::brass-util/t
                      aql-serial/to-aql)
                 (->> factory
                      ::brass-util/g
                      aql-serial/to-aql)
                 brass-data/qgf]
          cmd (->>  brass-data/query-demo
                    (into model)
                    (st/join "\n"))]
      (log/info "brass phase 2 demo: " cmd)
      (spit "brass_data.aql" cmd)
      (->> cmd
           aql-wrap/make-env
           (aql-wrap/extract-result brass-data/query-demo-attributes)
           json/write-str))))

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

(def PORT 9090)
(def brass-routes
  (apply http/routes
         (into [] cat [papa/aql-handlers
                       brass-handlers
                       papa/aql-fail-handlers])))

(defn -main [& args]
  (svr/run-server (hdlr/site #'brass-routes) {:port PORT})
  (log/info "server started. http://127.0.0.1:" PORT))
