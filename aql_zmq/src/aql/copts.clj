(ns aql.copts
  (:import [java.net InetAddress]))

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
