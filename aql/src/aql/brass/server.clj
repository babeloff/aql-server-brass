(ns aql.brass.server
  (:require
   (aql.brass [routes :as routes])
   (org.httpkit [server :as svr])
   (clojure.tools [logging :as log])
   (clojure.tools.nrepl [server :as nrs])
   (compojure [handler :as hdlr])
   (clojure.tools [cli :as cli]))
  (:import [org.apache.commons.daemon
            Daemon DaemonContext])
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon]))

(defonce IPADDR (atom "127.0.0.1"))
(defonce PORT (atom 9090))
(defonce NREPL (atom 7888))

(defonce state (atom ::stopped))
(defonce nrepl-server (atom nil))
(defonce main-server (atom nil))

(def cli-options
  [["-i" "--host IPADDR" "IP Address"
    :id :host
    :default "127.0.0.1"]
   ["-p" "--port PORT" "Port number"
    :id :port
    :default 9090
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "A number between 0 and 65536"]]
   ["-n" "--nrepl NREPL" "Port number"
    :id :nrepl
    :default 7888
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "A number between 0 and 65536"]]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"
    :id :help]])

(defn init [args]
  (let [{:keys [options]}
        (cli/parse-opts args cli-options)]
    (reset! IPADDR (:host options))
    (reset! PORT (:port options))
    (reset! NREPL (:nrepl options))
    (log/info "options" options)

    ; (reset! nrepl-server (nrs/start-server :port @NREPL_PORT))
    ; (log/info "nrepl server "
    ;          (str (get nrepl-server :ss))
    (reset! state ::initialized)))

(defn start []
  (log/info "aql server starting. " @IPADDR ":" @PORT)
  (reset! main-server
        (svr/run-server
         (hdlr/site #'routes/brass-routes)
         {:port @PORT :ip @IPADDR}))
  (reset! state ::running))

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
(defn -main [& args]
  ; (let [signal (java.util.concurrent.CountDownLatch. 1)])
  (init args) (start))
  ;(.await signal)
  ;(while (= ::running @state)
    ; (println @state)
;    (Thread/sleep 2000))
