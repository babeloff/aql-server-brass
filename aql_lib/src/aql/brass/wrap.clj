(ns aql.brass.wrap
  (:require
   (clojure [string :as st])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr])
   (aql [wrap :as aql-wrap])
   (aql.brass [data :as bd]
              [data-query :as bdq])))

;; gets used in aql.wrap


;; ::aql-wrap/tweek-output-xf : usage in xform-result

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

;; ::aql-wrap/sort-select-fn : usage query->sql-ent-helper

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

;; ::aql-wrap/pk-alias-lup : usage query->sql-equation-helper

(def helpers
  {::aql-wrap/ref-alias
   {"source"
    {::aql-wrap/pk "source_id"}
    "cot_event"
    {::aql-wrap/pk "id"
     ::aql-wrap/fk {"source_fk" "source_id"}}
    "cot_event_position"
    {::aql-wrap/pk "id"
     ::aql-wrap/fk {"cot_event_fk" "cont_event_id"}}}
   ::aql-wrap/sort-select-fn sort-select-fn})

(defn update-ref-alias-helper [permute]
  (sr/transform [::aql-wrap/ref-alias]
                #(merge % permute)
                helpers))

(defn xform-result
  [helpers reqs gen]
  (log/debug "transform-result" reqs)
  (let [env-map (aql-wrap/env->maps (sr/select-one [:env] gen))

        query-fn
        (fn [name]
          (aql-wrap/query->sql
           name helpers (get (::aql-wrap/query env-map) name)))

        schema-fn
        (fn [name]
          (aql-wrap/schema->sql
           name (get (::aql-wrap/schema env-map) name)))]
    {:query
     (into {}
           (map (fn [[cname q0 q1]]
                  (vector cname
                          {:t0 {:sql (query-fn q0)
                                :alias q0}
                           :t1 {:sql (query-fn q1)
                                :alias q1}})))
           (sr/select-one [aql-wrap/IS-QUERY] reqs))
     :schema
     (into []
           (map #(vector % (schema-fn %)))
           (sr/select-one [aql-wrap/IS-SCHEMA] reqs))
     :error
     (into []
           (map #(.getMessage %))
           (sr/select-one [aql-wrap/IS-ERR] gen))}))
