(ns aql.brass.client-demo 
    (:require 
        (org.httpkit [client :as clt])
        (clojure.data [json :as json])
        (clojure.tools [logging :as log])
        (aql.brass [data :as brass])))


(def options 
    {:query-params {:permutation (json/write-str brass/schema-mapping)}})
        
(defn -main [& args]
    ;(let [response (clt/post "http://localhost:9090/" "")]
    ;    (log/info "response's: " @response)
    (let [response (clt/post "http://localhost:9090/brass/p2/c1" options)]
        (log/info "response's: " @response)))
