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
   (aql [topics :as topics]
        [wrap :as aql-wrap]
        [copts :as copts]
        [handler :as handler])
   (clojure.tools.nrepl [server :as nrs]))
  (:import [org.zeromq ZMQ Utils]))

(defn usage [options-summary]
  (->> ["This server compiles aql programs."
        ""
        "Usage: java -jar program-jar [options]"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

;; http://zguide.zeromq.org/java:hwserver
(defn -main [& args]
  (let [[host port] (copts/validate-args args usage)
        ipname (.getCanonicalHostName host)
        address (str "tcp://" ipname ":" port)
        thread (Thread/currentThread)]
    (try
      (with-open [context (ZMQ/context 1)
                  responder (.socket context ZMQ/REP)]
        (log/info "aql responder starting. " address)
        (.bind responder address)
        (while (not (.isInterrupted thread))
          (let [req-str (.recvStr responder)
                request (json/read-str req-str)]
            (try
              (let [reply (handler/aql request)
                    status (.send responder (.getBytes reply) 0)]
                (log/debug "sent reply status" status))
              (catch Exception ex
                (log/error (if request request req-str) ex)
                (log/error "send reply"
                           (.send responder (.getBytes "failed") 0)))))))
      (catch Exception ex
        (log/error address ex)))))
