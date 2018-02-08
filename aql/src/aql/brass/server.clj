(ns aql.brass.server
  (:require
   (aql.brass [routes :as routes])
   (org.httpkit [server :as svr])
   (clojure.tools [logging :as log])
   (clojure.tools.nrepl [server :as nrs])
   (compojure [handler :as hdlr])))

(defonce nrepl-server (nrs/start-server :port 7888))
(log/info "nrepl server "
          (str (get nrepl-server :ss)))

(def PORT 9090)
(defn -main [& args]
  (svr/run-server (hdlr/site #'routes/brass-routes)
                  {:port PORT :ip "127.0.0.1"})
  (log/info "aql server started. http://127.0.0.1:" PORT))
