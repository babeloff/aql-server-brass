(ns aql.requestor
  (:require
   (clojure
    [pprint :as pp]
    [string :as st])
   (clojure.tools
    [logging :as log]
    [cli :as cli])
   (clojure.data [json :as json])
   (clojure.tools.nrepl [server :as nrs])
   (aql [data :as aql-data]
        [serialize :as serialize])
   (aql.demo [data :as data]))
  (:import [java.net InetAddress]
           [org.zeromq ZMQ Utils]
           [org.zeromq.ZMQ Socket]))

(def cli-options
  ;; An option with a required argument
  [["-i" "--hostname HOST" "IP host name"
    :default (InetAddress/getByName "localhost")
    :default-desc "localhost"
    :parse-fn #(InetAddress/getByName %)]
   ["-p" "--port PORT" "Port number"
    :id :port
    :default 9876
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "A number between 0 and 65536"]]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"
    :id :help]])

(def schema-mapping
  {:model (st/join "\n"
                   [aql-data/ts0
                    (serialize/to-aql data/schema-s)
                    data/qu0])
   :return {:query ["Q"]
            :schema ["S"]}})

(defn -main [& args]
    (let [parse-out (cli/parse-opts args cli-options)
          {:keys [options arguments errors summary]} parse-out
          host (:hostname options)
          port (:port options)]
      (pp/pprint schema-mapping)
      (let [address (str "tcp://" host ":" port)
            msg (.getBytes (json/write-str schema-mapping))]
        (with-open [context (ZMQ/context 1)
                    publisher (.socket context ZMQ/PUB)]
          (.bind publisher address)
          (.send publisher msg)))))
