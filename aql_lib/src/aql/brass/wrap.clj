(ns aql.brass.wrap
  (:require
   (clojure [string :as st])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (aql.brass [data :as bd]
              [data-query :as bdq])))


;; gets used in aql.wrap

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
         (log/debug "selection" attr)
         (get ordering (str attr) 0))
       (fn [lhs rhs]
         (log/debug "ordering" lhs rhs)
         (< lhs rhs))
       coll))))

;; :ref-alias-fn : usage query->sql-equation-helper
;; :sort-select-fn : usage query->sql-ent-helper
;; :tweek-output-xf : usage in xform-result
(def helpers
  {:ref-alias-fn (fn [ks] "TID")
   :sort-select-fn sort-select-fn
   :tweek-output-xf tweek-query-output})
