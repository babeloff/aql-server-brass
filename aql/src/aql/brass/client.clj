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

(def sample-submission-json
  {"martiServerModel"
   {"requirements"
    {"postgresqlPerturbation"
     {"tables"
      [{"table"  "cot_action"
        "columns"
         ["CotEvent_How"
          "CotEvent_ServerTime"
          "Position_PointCE"
          "Position_PointLE"
          "Position_TileX"
          "Position_Longitude"
          "Position_Latitude"]}
       {"table" "cot_detail"
        "columns"
        ["Position_PointHae"
         "CotEvent_Detail"
         "Position_TileY"
         "CotEvent_CotType"]}]}}}})

(def options
  {:method :post
   :headers {"content-type" "application/json; charset=UTF-8"}
   :body (->
          {:permutation sample-submission-json}
          json/write-str
          ring-io/string-input-stream)})

(defn -main [& args]
  (pp/pprint {:permutation sample-submission-json})
  (let [response (clt/post "http://localhost:9090/brass/p2/c1/json" options)]
    (-> @response
        :body
        json/read-str
        pp/pprint)))
