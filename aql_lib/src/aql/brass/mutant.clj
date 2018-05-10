(ns aql.brass.mutant
  (:require
   (aql [spec :as as])
   (aql.brass.spec [mutant :as ms])
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
        tables (conj mutant-tables ms/source)]
    {::ms/tables
     (into []
           (map (fn [{table "table" columns "columns"}]
                  {::as/name table
                   ::ms/columns
                   (mapv #(sr/select-one [%] ms/lookup) columns)}))
           tables)
     ::ms/references
     (let [tabv (into [] (into #{} (map (fn [{tab "table"}] tab)) tables))
           pairs (partition 2 1 tabv)]
       (into []
             (comp
              (map (fn [[lhs rhs]]
                     [[(str "has_" lhs) rhs lhs]
                      [(str "has_" rhs) lhs rhs]]))
              cat)
             pairs))}))
