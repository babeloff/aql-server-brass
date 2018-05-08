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

(def mutant-json-def
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
          "Position_Latitude"]}{}]}}}})

(def mutant-json-live-1
  {"martiServerModel"
     {"requirements"
      {"postgresqlPerturbation"
       {"tables"
        [{"table" "ab9254b78c81c4303a2bac778a67343d8"
          "columns"
          ["source_id" "cot_type" "how" "detail" "servertime"]}
         {"table" "ae3962ebf18084c0f95329137329b88d8",
          "columns"
          ["point_hae" "point_ce" "point_le"
           "tileX" "tileY" "longitude" "latitude"]}]}}}})

(def mutant-json mutant-json-live-1)

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
