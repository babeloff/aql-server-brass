(ns aql.brass.topics
  (:require
   (clojure [string :as st]
            [pprint :as pp])
   (clojure.data [json :as json])
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr])
   (aql [wrap :as aql-wrap]
        [serialize :as aql-serial])
   (aql.brass
    [demo :as brass-data]
    [cospan :as brass-cospan]
    [mutant :as brass-mutant]
    [wrap :as brass-wrap])
   (aql.brass.spec [mutant :as brass-spec])))

(defn brass-p2c1
  [p-json]
  (let [mutant (brass-mutant/normalize p-json)
        factory (brass-cospan/factory
                 {::brass-spec/s brass-data/schema-s
                  ::brass-spec/x brass-data/schema-x
                  ::brass-spec/f brass-data/mapping-f
                  ::brass-spec/mutant mutant})
        typeside [brass-data/ts-sql1]
        model [(->> factory
                    ::brass-cospan/s
                    aql-serial/to-aql)
               (->> factory
                    ::brass-cospan/x
                    aql-serial/to-aql)
               (->> factory
                    ::brass-cospan/f
                    aql-serial/to-aql)
               (->> factory
                    ::brass-cospan/t
                    aql-serial/to-aql)
               (->> factory
                    ::brass-cospan/g
                    aql-serial/to-aql)
               brass-data/qgf]
        cmd (st/join "\n"
              (sequence cat [typeside
                             model
                             brass-data/query-demo]))]
    (log/info "see file 'brass_data.aql': ")
    (spit "brass_data.aql" (str cmd "\n"))
    (try
      (let [gen (aql-wrap/generate cmd)
            helpers (brass-wrap/update-ref-alias-helper
                     (get factory ::brass-cospan/key-alias))]
        (log/info "brass phase 2 results: " gen helpers)
        (->> gen
          (brass-wrap/xform-result
            helpers
            brass-data/demo-mutants)
          json/write-str))
      (catch Exception ex
        (log/error "brass aql fault " ex)
        (->>
         {:status "aql-brass-error"
          :msg (.getMessage ex)}
         json/write-str)))))
