(ns aql.demo.client
  (:require
   (org.httpkit [client :as clnt])
   (clojure.data [json :as json])
   (clojure
    [string :as st]
    [pprint :as pp])
   (clojure.tools [logging :as log])
   (ring.util [io :as ring-io])
   (aql [data :as aql-data]
        [serialize :as serialize])
   (aql.demo [data :as data])))

(def schema-mapping
  {:model (st/join "\n"
                   [aql-data/ts0
                    (serialize/to-aql data/schema-s)
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
  (pp/pprint schema-mapping)
  (let [response (clnt/post "http://localhost:9090/aql/json" options)]
    (-> @response
        :body
        json/read-str
        pp/pprint)))
