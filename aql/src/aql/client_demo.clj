(ns aql.client-demo 
    (:require 
        (org.httpkit [client :as clt])
        (clojure.data [json :as json])
        (clojure.tools [logging :as log])))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"
(def schema-map
    { "martiServerModel" 
        { "requirements"
            { "postgresqlPerturbation" 
                { "tables"
                    [ { "name" "CotEvent" 
                        "columns" 
                            [   "CotEvent_SourceId",
                                "CotEvent_How",
                                "CotEvent_ServerTime",
                                "Position_PointCE",
                                "Position_PointLE",
                                "Position_TileX",
                                "Position_Longitude",
                                "Position_Latitude"]}
                    
                      { "name" "CotEventDetail]"
                        "columns"
                              [   "Position_PointHae"
                                  "CotEvent_Detail"
                                  "Position_TileY"
                                  "CotEvent_CotType"]}]}}}})


(def options 
    {:query-params {:permutation (json/write-str schema-map)}})
        
(defn -main [& args]
    ;(let [response (clt/post "http://localhost:9090/" "")]
    ;    (log/info "response's: " @response)
    (let [response (clt/post "http://localhost:9090/brass/p2/c1" options)]
        (log/info "response's: " @response)))
