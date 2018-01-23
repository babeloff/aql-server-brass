(ns aql.demo.client 
    (:require 
        (org.httpkit [client :as clnt])
        (clojure.data [json :as json])
        (clojure 
            [string :as st]
            [pprint :as pp])
        (clojure.tools [logging :as log])
        (ring.util [io :as ring-io])
        (aql.demo [data :as data])
        (aql [util :as util])))

(def schema-mapping
    {:model (st/join "\n"
                [   data/ts0 
                    (util/serialize-aql-schema data/sc-s)
                    data/qu0])                             
     :return {:query ["Q"]
              :schema ["S"]}})
                        

(def options 
    {:method :post 
     :headers {"content-type" "application/json; charset=UTF-8"}
     :body (-> schema-mapping
                json/write-str
                ring-io/string-input-stream)})
        
(defn -main [& args]
    (let [response (clnt/post "http://localhost:9090/aql/json" options)]
        (-> @response 
            :body
            json/read-str
            pp/pprint)))
            
