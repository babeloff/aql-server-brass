(ns aql.demo.client 
    (:require 
        (org.httpkit [client :as clnt])
        (clojure.data [json :as json])
        (clojure.tools [logging :as log])
        (aql.demo [data :as data])
        (aql [util :as util])))

(def schema-mapping
    {:model (str data/ts0 " "
                (util/serialize-aql-schema data/sc0) " "
                data/qu0)
                
     :return {:query ["Q"]
              :schema ["S"]}})
                        

(def options 
    {:query-params {:action (json/write-str schema-mapping)}})
        
(defn -main [& args]
    (let [response (clnt/post "http://localhost:9090/aql" options)]
        (log/info "response's: " @response)))
