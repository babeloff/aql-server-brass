(ns aql.brass.wrap
  (:require
   (clojure [string :as st])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (aql [wrap :as aw])
   (aql.brass [data :as bd]
              [data-query :as bdq])))


;; gets used in aql.wrap


;; ::aw/tweek-output-xf : usage in xform-result

(defn tweek-query-output
  "a transducer that 'tweeks' the key value
  to be the qname for the class if one has
  been supplied."
  [xf]
  (fn
    ([] (xf))
    ([res] (xf res))
    ([res [key value]]
     (xf res
         (let [qkey (get bd/query-class-names key key)]
           (vector qkey
             {"class" qkey
              "sql" value
              "aid" key}))))))
;; TODO add the original sql and the aql

;; ::aw/sort-select-fn : usage query->sql-ent-helper

(let [q-lup
      (into {}
            (map (fn [[key value]]
                   (vector
                     key
                     (into {}
                           (map
                             vector
                             (::bdq/select-order value)
                             (range))))))
            bd/query-dict)]
  (defn sort-select-fn [query-key coll]
    (let [ordering (get q-lup query-key)]
      (log/debug "sort-select" query-key ordering)
      (sort-by
       (fn [attr]
         ;(log/debug "selection" attr)
         (get ordering (str attr) 10))
       (fn [lhs rhs]
         (if (= 10 lhs) (log/debug "ordering lhs" lhs))
         (if (= 10 rhs) (log/debug "ordering rhs" rhs))
         (< lhs rhs))
       coll))))

;; ::aw/ref-alias-fn : usage query->sql-equation-helper

(let [alias-id {"source" "source_id"
                "cot_event" "cot_event_id"
                "cot_event_position" "cot_position_id"}]
  (defn ref-alias-fn [from-alias key-type key-name]
    (let [key-str (str key-name)]
      (case key-type
        ::aw/pk
        (get alias-id (get from-alias key-str) "PKID")
        "UnkRef"))))


(def helpers
  {::aw/ref-alias-fn ref-alias-fn
   ::aw/sort-select-fn sort-select-fn
   ::aw/tweek-output-xf tweek-query-output})
