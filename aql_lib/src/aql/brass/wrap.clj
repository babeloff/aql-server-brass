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

;; ::aw/pk-alias-lup : usage query->sql-equation-helper

(def helpers
  {::aw/ref-alias
    {"source"
     {::aw/pk "source_id"}
     "cot_event"
     {::aw/pk "id"
      ::aw/fk {"has_source" "source_id"}}
     "cot_event_position"
     {::aw/pk "id"
      ::aw/fk {"has_cot_event" "cont_event_id"}}
     "cot_action"
     {::aw/pk "id"
      ::aw/fk {"has_source" "source_id"
               "has_cot_detail" "id"}}
     "cot_detail"
     {::aw/pk "id"
      ::aw/fk {"has_cot_action" "id"}}}
   ::aw/sort-select-fn sort-select-fn
   ::aw/tweek-output-xf tweek-query-output})
