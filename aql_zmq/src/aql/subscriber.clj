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
   (aql [topics :as topics]
        [wrap :as aql-wrap]
        [copts :as copts]
        [handler :as handler])
   (clojure.tools.nrepl [server :as nrs]))
  (:import [org.zeromq ZMQ Utils]))

(defn usage [options-summary]
  (->> ["This server handles brass specific aql mutation requests."
        ""
        "Usage: java -jar program-jar [options]"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn -main [& args]
  (try
    (let [[host port] (copts/validate-args [args])
          ipaddr (.getCanonicalHostAddress host)
          address (str "tcp://" ipaddr ":" port)
          thread (Thread/currentThread)]
      (with-open [context (ZMQ/context 1)
                  subscriber (.socket context ZMQ/SUB)]
        (log/info "aql subscriber starting. " address)
        (doto subscriber
              (.connect address)
              (.subscribe ZMQ/SUBSCRIPTION_ALL))
        (while (not (.isInterrupted thread))
          (let [req-str (.recvStr subscriber)
                request (json/read-str req-str)]
            (try
              (let [reply (handler/aql request)]
                (log/debug "show reply" reply))
              (catch Exception ex
                (log/error (if request request req-str) ex)))))))
    (catch Exception ex (ex-data ex))))
