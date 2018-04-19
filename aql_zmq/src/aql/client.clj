(ns aql.client
  (:require
   (clojure
    [pprint :as pp]
    [string :as string])
   (clojure.tools
    [logging :as log]
    [cli :as cli])
   (clojure.tools.nrepl [server :as nrs]))
  (:import [java.net InetAddress]
           [org.zeromq ZMQ Utils]
           [org.zeromq.ZMQ Socket]))


(let [context (ZMQ/context 1)
      address = "tcp://localhost:" + Utils.findOpenPort();
      msg (.getBytes "abc")
      publisher (.socket context ZMQ/PUB)]
  (.bind publisher address)
  (.send publisher msg)
  (.close publisher)
  (.close context))
