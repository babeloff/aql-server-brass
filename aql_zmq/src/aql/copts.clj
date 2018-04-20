(ns aql.copts
  (:require
    (clojure.tools
     [logging :as log]
     [cli :as cli])
    (clojure
     [pprint :as pp]
     [string :as string]))
  (:import [java.net InetAddress]
           [org.slf4j LoggerFactory]
           [ch.qos.logback.core.util StatusPrinter]))

(def cli-options
  [["-i" "--hostname HOST" "IP host name"
    :default (InetAddress/getByName "localhost")
    :default-desc "localhost"
    :parse-fn #(InetAddress/getByName %)]
   ["-p" "--port PORT" "Port number"
    :id :port
    :default 9876
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

(defn show-logback-config []
  (StatusPrinter/print (LoggerFactory/getILoggerFactory)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  "Validate command line arguments.
  Either return a map indicating the program
  should exit (with a error message, and optional ok status), or
  a map indicating the options provided."
  [args usage]
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
