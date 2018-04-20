(ns aql.responder
  (:require
   (clojure
    [pprint :as pp]
    [string :as string])
   (clojure.tools
    [logging :as log]
    [cli :as cli])
   (clojure.data [json :as json])
   (com.rpl [specter :as sr])
   (aql [wrap :as aql-wrap]
        [copts :as copts])
   (clojure.tools.nrepl [server :as nrs]))
  (:import [org.zeromq ZMQ Utils]
           [org.zeromq.ZMQ Socket]))

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
        (cli/parse-opts args copts/cli-options)]
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

(defn dummy-handler [request]
  (println request)
  true)

;; http://zguide.zeromq.org/java:hwserver
(defn -main [& args]
  (try
    (let [[host port] (validate-args [args])
          ipaddr (.getHostAddress host)
          address (str "tcp://" ipaddr ":" port)
          thread (Thread/currentThread)]
      (with-open [context (ZMQ/context 1)
                  responder (.socket context ZMQ/REP)]
        (log/info "aql server starting. " address)
        (doto responder
             (.bind address))
        (println "STATE:[RUNNING]")
        (.flush *out*)
        (while (not (.isInterrupted thread))
          (let [req-str (.recvStr responder)
                request (json/read-str req-str)
                response (dummy-handler request)
                status (.send responder response)]
            (println status)))))

    (catch Exception ex (ex-data ex))))
