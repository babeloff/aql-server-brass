(ns aql.brass.server
  (:require
   (aql.brass [routes :as routes])
   (org.httpkit [server :as svr])
   (clojure.tools [logging :as log])
   (clojure.tools.nrepl [server :as nrs])
   (compojure [handler :as hdlr]))
  (:import [org.apache.commons.daemon
            Daemon DaemonContext])
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon]))

(def IP "127.0.0.1")
(def PORT 9090)
(def NREPL_PORT 7888)

(defonce state (atom ::stopped))
(defonce nrepl-server (atom nil))
(defonce main-server (atom nil))

(defn init [args]
  ; (reset! nrepl-server (nrs/start-server :port NREPL_PORT))

  ; (log/info "nrepl server "
  ;          (str (get nrepl-server :ss))
  (reset! state ::initialized))

(defn start []
  (log/info "aql server starting. " IP ":" PORT)
  (reset! main-server
        (svr/run-server
         (hdlr/site #'routes/brass-routes)
         {:port PORT :ip IP}))
  (reset! state ::running)

  (while (= ::running @state)
    ; (println @state)
    (Thread/sleep 2000)))

(defn stop []
  (reset! state ::stopped)
  (when-not (nil? @main-server)
    (@main-server :timeout 1000)
    (reset! main-server nil)))

;; daemon implementation
(defn -init [this ^DaemonContext context]
  (init (.getArguments context)))
(defn -start [this] (future (start)))
(defn -stop [this] (stop))
(defn -main [& args] (init args) (start))
