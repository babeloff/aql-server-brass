(ns aql.service
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

(defonce HOST (atom "localhost"))
(defonce PORT (atom 9090))
(defonce NREPL (atom 7888))

(defonce STATE (atom ::stopped))
(defonce NREPL-SERVER (atom nil))
(defonce MAIN-SERVER (atom nil))

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
  (->> ["This server handles brass specific aql mutation requests."
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

        ; (reset! NREPL-SERVER (nrs/start-server :port @NREPL_PORT))
        ; (log/info "nrepl server "
        ;          (str (get NREPL-SERVER :ss))
        (reset! STATE ::initialized)))))

(defn start [router]
  (let [ipaddr (.getHostAddress @HOST)
        port @PORT
        context (ZMQ/context 1)
        address (str "tcp://localhost:" port)
        subscriber (.socket context ZMQ/SUB)]
    (log/info "aql server starting. " address)
    (doto subscriber
          (.connect address)
          (.subscribe ZMQ/SUBSCRIPTION_ALL))
    subscriber.recv();
    (reset! MAIN-SERVER
            (svr/run-server
             (hdlr/site router)
             {:port port :ip ipaddr}))
    (.close subscriber)
    (.close context)
    (println "STATE:[RUNNING]")
    (.flush *out*)))

(defn stop []
  (reset! STATE ::stopped)
  (when-not (nil? @MAIN-SERVER)
    (@MAIN-SERVER :timeout 1000)
    (reset! MAIN-SERVER nil)))
