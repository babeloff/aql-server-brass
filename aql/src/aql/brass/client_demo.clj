(ns aql.brass.client-demo 
    (:require 
        (org.httpkit [client :as clt])
        (ring.util [io :as ring-io])
        (clojure 
            [string :as st]
            [pprint :as pp])
        (clojure.data [json :as json])
        (clojure.tools [logging :as log])
        (aql.brass [data :as brass-data])))

(def options 
    {:method :post 
        :headers {"content-type" "application/json; charset=UTF-8"}
        :body (-> 
                {:permutation brass-data/schema-perturbation}
                json/write-str
                ring-io/string-input-stream)})
        
(defn -main [& args]
    (let [response (clt/post "http://localhost:9090/brass/p2/c1/json" options)]
        (log/info "response's: " @response)))
