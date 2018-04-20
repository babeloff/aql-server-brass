(ns aql.subscriber
  (:require
   (clojure
    [pprint :as pp]
    [string :as string])
   (clojure.tools
    [logging :as log]
    [cli :as cli])
   (clojure.data [json :as json])
   (com.rpl [specter :as sr])
   (aql [wrap :as aql-wrap])
   (clojure.tools.nrepl [server :as nrs]))
  (:import [java.net InetAddress]
           [org.zeromq ZMQ Utils]
           [org.zeromq.ZMQ Socket]))

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
      (throw (ex-info (usage summary) {:ok? true}))

      errors ; errors => exit with description of errors
      (throw (ex-info (error-msg errors) {:ok? false}))

      ; failed custom validation => exit with usage summary
      (< 1 (count arguments))
      (throw (ex-info (usage summary) {:ok? false}))

      :else
      [(:hostname options) (:port options)])))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn aql-handler [request]
  (log/info "aql-handler")
  (if-let [action (sr/select-one [:body] request)]
    (let [model (sr/select-one ["model"] action)
          aql-env (aql-wrap/generate (str model))
          return-objs (sr/select-one ["return"] action)]
      (log/info "aql-handler:" return-objs)
      (->> aql-env
           (aql-wrap/xform-result return-objs identity)
           json/write-str))))

(defn -main [& args]
  (try
    (let [more-req? (atom true)
          [host port] (validate-args [args])
          ipaddr (.getHostAddress host)
          address (str "tcp://" ipaddr ":" port)]
      (with-open [context (ZMQ/context 1)
                  subscriber (.socket context ZMQ/SUB)]
        (log/info "aql server starting. " address)
        (doto subscriber
              (.connect address)
              (.subscribe ZMQ/SUBSCRIPTION_ALL))
        (println "STATE:[RUNNING]")
        (.flush *out*)
        (while @more-req?
          (let [req-str (.recvStr subscriber)
                request (json/read-str req-str)]
            (println request)))))
    (catch Exception ex (ex-data ex))))
