(ns aql.daemon
  (:require
   (aql [service :as util])
   (aql [routes :as routes])
   (clojure.tools [logging :as log]))
  (:import [org.apache.commons.daemon
            Daemon DaemonContext])
  (:gen-class
    :implements [org.apache.commons.daemon.Daemon]))

;; daemon implementation
(defn -init [this ^DaemonContext context]
  (util/init (.getArguments context)))
(defn -start [this] (future (util/start)))
(defn -stop [this] (util/stop))

(defn -main [& args]
  ; (let [signal (java.util.concurrent.CountDownLatch. 1)])
  (util/init args) (util/start #'routes/aql-routes))
