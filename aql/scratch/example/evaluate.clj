

;; mucking around with Ryan's sample
(require '(clojure.data [json :as json]))
;; (require '[aql.example.data :as data] :reload)
(require '[aql.wrap :as wrap] :reload)
(require '[aql.demo.client :as client] :reload)


(def aql-env (wrap/make-env (:model client/schema-mapping)))
(def return (json/read-str (json/write-str (:return client/schema-mapping))))

(wrap/extract-result aql-env return)

;; (def init (try-parse prog0))

;;(def start (System/currentTimeMillis))
;;(def evn (make-env prog0 init))
;;(def middle (System/currentTimeMillis))
