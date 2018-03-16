(ns aql.brass.mutant
  (:require
   (aql [spec :as aql-spec])
   (aql.brass.spec [mutant :as mut-spec])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])))

(defn normalize
  "normalize a partial json mutant object.
   it should at a minimum conform to
   aql.brass.spec.mutant/::mutant
   see scratch.brass.permute.clj for an example"
  [permutation-json]
  (let [mutant-tables
        (sr/select-one
                ["martiServerModel"
                 "requirements"
                 "postgresqlPerturbation"
                 "tables"]
                permutation-json)
        tables (conj mutant-tables mut-spec/source)]
    {::mut-spec/tables
     (into []
           (map (fn [{table "table" columns "columns"}]
                  {::aql-spec/name table
                   ::mut-spec/columns
                   (mapv #(sr/select-one [%] mut-spec/lookup) columns)}))
           tables)
     ::mut-spec/references
     (let [tabv (into [] (into #{} (map (fn [{tab "table"}] tab)) tables))
           pairs (partition 2 1 tabv)]
       (into []
             (comp
              (map (fn [[lhs rhs]]
                     [[(str "has_" lhs) rhs lhs]
                      [(str "has_" rhs) lhs rhs]]))
              cat)
             pairs))}))
