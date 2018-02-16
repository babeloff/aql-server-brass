(ns aql.server
  (:require
   (aql [routes :as routes])
   (org.httpkit [server :as svr])
   (clojure.tools [logging :as log])
   (clojure.tools.nrepl [server :as nrs])
   (compojure [handler :as hdlr]))
  (:import [org.apache.commons.daemon
            Daemon DaemonContext])
  (:gen-class
    :implements [org.apache.commons.daemon.Daemon]))

(defonce nrepl-server (nrs/start-server :port 7888))
(log/info "nrepl server "
          (str (get nrepl-server :ss)))

(defonce main-server (atom nil))

(defn stop-server []
  (when-not (nil? @main-server)
    (@main-server :timeout 100)
    (reset! main-server nil)))

(def IP "127.0.0.1")
(def PORT 9090)
(defn -main [& args]
  (log/info "aql server starting. " IP ":" PORT)
  (reset! main-server
          (svr/run-server
           (hdlr/site #'routes/aql-routes)
           {:port PORT :ip IP})))
