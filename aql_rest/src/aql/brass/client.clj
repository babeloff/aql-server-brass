(ns aql.brass.client
  (:require
   (org.httpkit [client :as clt])
   (ring.util [io :as ring-io])
   (clojure
    [string :as st]
    [pprint :as pp])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (aql.brass [data :as brass-data])))

(def mutant-json
  {"martiServerModel"
   {"requirements"
    {"postgresqlPerturbation"
     {"tables"
      [{"table"  "cot_action"
        "columns"
         ["Event_Id"
          "Event_SourceId"
          "Event_How"
          "Event_ServerTime"
          "Position_PointCE"
          "Position_PointLE"
          "Position_tilex"
          "Position_Longitude"
          "Position_Latitude"]}
       {"table" "cot_detail"
        "columns"
        ["Position_Id"
         "Position_EventId"
         "Position_PointHae"
         "Event_Detail"
         "Position_tiley"
         "Event_CotType"]}]}}}})

(def options
  {:method :post
   :headers {"content-type" "application/json; charset=UTF-8"}
   :body (->
          {:permutation mutant-json}
          json/write-str
          ring-io/string-input-stream)})

(defn -main [& args]
  (pp/pprint {:permutation mutant-json})
  (let [response (clt/post "http://localhost:9090/brass/p2/c1/json" options)]
    (-> @response
        :body
        json/read-str
        pp/pprint)))
