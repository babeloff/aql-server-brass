(ns aql.brass.client-demo 
    (:require 
        (org.httpkit [client :as clt])
        (clojure.data [json :as json])
        (clojure.tools [logging :as log])))

;; https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk/
;;  docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
;;  "Sample SubmissionModel value"
(def schema-mapping
    { "martiServerModel" 
        { "requirements"
            { "postgresqlPerturbation" 
                { "tables"
                    [ { "name" "cot_event" 
                        "columns" 
                            [   ["cot_event" "source_id"]
                                ["cot_event" "how"]
                                ["cot_event" "servertime"]
                                ["cot_event_position" "point_ce"]
                                ["cot_event_position" "point_le"]
                                ["cot_event_position" "tileX"]
                                ["cot_event_position" "longitude"]
                                ["cot_event_position" "latitude"]]}
                    
                      { "name" "cot_event_detail]"
                        "columns"
                              [   ["cot_event_position" "point_hae"]
                                  ["cot_event" "detail"]
                                  ["cot_event_position" "tileY"]
                                  ["cot_event" "cot_type"]]}]}}}})


(def options 
    {:query-params {:permutation (json/write-str schema-mapping)}})
        
(defn -main [& args]
    ;(let [response (clt/post "http://localhost:9090/" "")]
    ;    (log/info "response's: " @response)
    (let [response (clt/post "http://localhost:9090/brass/p2/c1" options)]
        (log/info "response's: " @response)))
