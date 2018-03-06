(ns aql.brass.server
  (:require
   (aql.brass [routes :as routes])
   (org.httpkit [server :as svr])
   (clojure.tools [logging :as log])
   (clojure.tools.nrepl [server :as nrs])
   (compojure [handler :as hdlr])
   (clojure.tools [cli :as cli])
   (clojure [string :as string]))
  (:import [org.apache.commons.daemon
            Daemon DaemonContext]
           [java.net InetAddress])
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon]))

(defonce HOST (atom "localhost"))
(defonce PORT (atom 9090))
(defonce NREPL (atom 7888))

(defonce state (atom ::stopped))
(defonce nrepl-server (atom nil))
(defonce main-server (atom nil))

(def cli-options
  [["-i" "--hostname HOST" "IP host name"
    :default (InetAddress/getByName "localhost")
    :default-desc "localhost"
    :parse-fn #(InetAddress/getByName %)]
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

(defn usage [options-summary]
  (->> ["This server handles brass specific aql perturbation requests."
        ""
        "Usage: java -jar program-jar [options]"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  "Validate command line arguments.
  Either return a map indicating the program
  should exit (with a error message, and optional ok status), or
  a map indicating the options provided."
  [args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}

      ; failed custom validation => exit with usage summary
      (< 1 (count arguments))
      {:exit-message (usage summary)}

      :else
      {:options options})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn init [args]
  (let [{:keys [options exit-message ok?]}
        (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (do
        (reset! HOST (:hostname options))
        (reset! PORT (:port options))
        (reset! NREPL (:nrepl options))
        (log/info "options" args options)

        ; (reset! nrepl-server (nrs/start-server :port @NREPL_PORT))
        ; (log/info "nrepl server "
        ;          (str (get nrepl-server :ss))
        (reset! state ::initialized)))))

(defn start []
  (let [ipaddr (.getHostAddress @HOST)
        port @PORT]
    (log/info "aql server starting. " ipaddr ":" port)
    (reset! main-server
            (svr/run-server
             (hdlr/site #'routes/brass-routes)
             {:port port :ip ipaddr}))
    (reset! state ::running)
    (println "STATE:[RUNNING]")
    (.flush *out*)))

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
