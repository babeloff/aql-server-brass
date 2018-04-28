
(require '(clojure
           [pprint :as pp]
           [string :as st]))
(require '(com.rpl [specter :as sr]))

(require '(aql [data :as aql-data]
               [serialize :as serialize]
               [wrap :as aql-wrap]) :relaod)
(require '(aql.demo [data :as data]) :reload)
(require '[aql.responder :as resp] :reload)
(require '(aql.brass [data :as brass-data]) :reload)
(require '(aql.brass [wrap :as brass-wrap]) :reload)
(require '(aql.brass [data_query :as brass-data-query]) :reload)

(def schema-mapping
  {"topic" "aql/program/eval"
   "model" (st/join "\n"
                    [aql-data/ts0
                     (serialize/to-aql data/schema-s)
                     data/qu0])
   "return" {"query" ["Q"]
             "schema" ["S"]}})
(def request schema-mapping)
(get request "topic")
(resp/aql-handler schema-mapping)

(def model (sr/select-one ["model"] request))
(def gen (aql-wrap/generate (str model)))
(def return-objs (sr/select-one ["return"] request))

(def env (sr/select-one [:env] gen))
(def env-map (aql-wrap/env->maps (sr/select-one [:env] gen)))
(def query-fn (fn [name] (get (::aql-wrap/query env-map) name)))
; (def schema-fn (partial get (::aql-wrap/schema env-map)))
(def query (->> "Q" query-fn))

(let [env-map (aql-wrap/env->maps (sr/select-one [:env] gen))
      query-fn (fn [name] (aql-wrap/query->sql (get (::aql-wrap/query env-map) name)))
      schema-fn (fn [name] (aql-wrap/schema->sql (get (::aql-wrap/schema env-map) name)))]
  {:query (into []
                (comp
                 (sr/traverse-all ["query"])
                 (mapcat #(vector % (query-fn %)))
                 identity)
                [return-objs])
   :schema (into []
                 (comp
                  (sr/traverse-all ["schema"])
                  (mapcat #(vector % (schema-fn %))))
                 [return-objs])})
(def helpers
  {:ref-alias-fn (fn [ks] "DID")
   :sort-select-fn identity})

(aql-wrap/xform-result helpers return-objs identity gen)
