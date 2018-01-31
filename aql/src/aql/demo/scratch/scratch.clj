

;; mucking around with Ryan's sample
(require '(clojure.data [json :as json]))
(require '[aql.demo.data :as demo] :reload)
(require '[aql.util :as util] :reload)
(require '[aql.demo.client :as client] :reload)


;; (util/serialize-aql-schema demo/schema-s)
(def aql-env (util/make-env (:model client/schema-mapping)))
(def return (json/read-str (json/write-str (:return client/schema-mapping))))

(util/extract-result aql-env return)

;; (def init (try-parse prog0))

;;(def start (System/currentTimeMillis))
;;(def evn (make-env prog0 init))
;;(def middle (System/currentTimeMillis))
