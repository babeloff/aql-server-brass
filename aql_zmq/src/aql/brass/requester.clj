(ns aql.requester
  (:require
   (clojure
    [pprint :as pp]
    [string :as st])
   (clojure.tools
    [logging :as log]
    [cli :as cli])
   (clojure.data [json :as json])
   (clojure.tools.nrepl [server :as nrs])
   (aql [data :as aql-data]
        [serialize :as serialize]
        [copts :as copts])
   (aql.demo [data :as data]))
  (:import [org.zeromq ZMQ Utils]))

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
            "Position_TileX"
            "Position_Longitude"
            "Position_Latitude"]}
         {"table" "cot_detail"
          "columns"
          ["Position_Id"
           "Position_EventId"
           "Position_PointHae"
           "Event_Detail"
           "Position_TileY"
           "Event_CotType"]}]}}}})


;; http://zguide.zeromq.org/java:hwclient
(defn -main [& args]
    (let [parse-out (cli/parse-opts args copts/cli-options)
          {:keys [options arguments errors summary]} parse-out
          _ (if errors (log/warn arguments errors summary))
          host (.getCanonicalHostName (:hostname options))
          port (:port options)]
      (log/debug mutant-json)
      (let [address (str "tcp://" host ":" port)
            with-topic {"topic" "brass/p2/c1/json"
                        "payload" mutant-json}
            msg-str (json/write-str with-topic)]
        (log/info "aql requester starting: " address)
        (with-open [context (ZMQ/context 1)
                    requester (.socket context ZMQ/REQ)]
          (.connect requester address)
          (.send requester (.getBytes msg-str))
          (let [reply (.recvStr requester)]
            (println "received " reply))))))
