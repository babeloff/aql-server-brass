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

(def schema-mapping
  {:topic "aql/program/eval"
   :model (st/join "\n"
                   [aql-data/ts0
                    (serialize/to-aql data/schema-s)
                    data/qu0])
   :return {:query ["Q"]
            :schema ["S"]}})

;; http://zguide.zeromq.org/java:hwclient
(defn -main [& args]
    (let [parse-out (cli/parse-opts args copts/cli-options)
          {:keys [options arguments errors summary]} parse-out
          _ (if errors (log/warn arguments errors summary))
          host (.getCanonicalHostName (:hostname options))
          port (:port options)]
      (log/debug schema-mapping)
      (let [address (str "tcp://" host ":" port)
            msg-str (json/write-str schema-mapping)]
        (log/info "aql requester starting: " address)
        (with-open [context (ZMQ/context 1)
                    requester (.socket context ZMQ/REQ)]
          (.connect requester address)
          (.send requester (.getBytes msg-str))
          (let [reply (.recvStr requester)]
            (println "received " reply))))))
