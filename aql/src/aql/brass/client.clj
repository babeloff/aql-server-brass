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

(def options
  {:method :post
   :headers {"content-type" "application/json; charset=UTF-8"}
   :body (->
          {:permutation brass-data/sample-submission-json}
          json/write-str
          ring-io/string-input-stream)})

(defn -main [& args]
  (pp/pprint {:permutation brass-data/sample-submission-json})
  (let [response (clt/post "http://localhost:9090/brass/p2/c1/json" options)]
    (-> @response
        :body
        json/read-str
        pp/pprint)))
