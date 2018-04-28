(require '(clojure [pprint :as pp] [string :as st]))
(require '(clojure.data [json :as json]))
(require '(clojure.tools [logging :as log]))
;(require '[string :as st])
(require '(com.rpl [specter :as sr]))
(require '(aql.brass [data :as brass-data]))
(require '(aql [util :as aql-util]))

(require '(aql [wrap :as aql-wrap]) :reload)

;; working with the p2 cp1 brass demo
;; (require '[aql.serialize :as ser] :reload)

(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))
(import '(catdata LineException))
(def model (slurp "brass_data.aql"))

(def gen (aql-wrap/generate model))
; (def drvr (aql-wrap/make-driver model)) (.start drvr)
; (def exn (aql-wrap/private-field "exn" drvr))
(def env (sr/select-one [:env] gen))
(def reqs (merge brass-data/demo-objects))
(def env-map (aql-wrap/env->maps (sr/select-one [:env] gen)))
(def query-fn (partial get (::aql-wrap/query env-map)))
; (def schema-fn (partial get (::aql-wrap/schema env-map)))
(sr/select-one [:query] reqs)
(def query (->> "Qt_01" query-fn))
(def full-query (.unnest query))
; (def qs (.second (.toSQLViews full-query "" "" "ID" "char")))

(def ctx full-query)
(def schema (.dst full-query))
(def ents (.ens full-query))
(def attrs (.atts full-query))
(def refs (.fks full-query))
(def ent-names (map #(.str %) (.keySet ents)))
(def ent-keys (.keySet ents))
(def ent-key (first ent-keys))
(def ent-name (.str ent-key))
(def b (.get ents ent-key))
(def gens (.gens b))
(def eqns (.eqs b))
(def eqn (first eqns))
(def lhs (.first eqn))
(aql-wrap/query->sql-path-helper ctx lhs "ID")


(def is-empty? (.isEmpty gens))
(def from
      (into []
            (map (fn [ent] (str (.get gens ent) " as " ent)))
            (.keySet gens)))

(def select-attr
      (into []
            (map (fn [attr] (str (.get attrs attr) " as " attr)))
            (.attsFrom schema ent-key)))

(def select-ref
      (into []
            (map (fn [ref] (str (.get refs ref) " as " ref)))
            (.fksFrom schema ent-key)))
(aql-wrap/to-sql-helper schema ents ent-key attrs refs)
(aql-wrap/to-sql full-query)
(def query-names (.ens (.dst query)))

(aql-wrap/query->sql query)

(into [] (comp (sr/traverse-all [:err])) [gen])
(into [] (comp (map #(.getMessage %))) (sr/traverse [:err sr/ALL] gen))
(defn ref-alias-fn [ks] "AID")
(def result (aql-wrap/xform-result {:ref-alias-fn ref-alias-fn} reqs identity gen))
((aql-util/echo log/info "result ") result)

(def env-map (aql-wrap/env->maps (sr/select-one [:env] gen)))
(def queries (sr/select-one [:query] reqs))
(def query-results (map #(vector % (aql-wrap/env->query->sql env-map %)) queries))
(aql-wrap/env->query->sql env-map "Qs_01")
(::aql-wrap/query env-map)
