(ns aql.brass.mutant
  (:require
   (aql [spec :as as])
   (aql.brass.spec [mutant :as ms])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])))

(defn normalize-helper [addendum]
  (fn [{table "table" columns "columns"}]
    {::as/name table
     ::ms/columns
     (into
      addendum
      (map #(sr/select-one [%] ms/lookup))
      columns)}))

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
        event-id (sr/select-one ["Event_Id"] ms/lookup)]
    {::ms/tables
     (into
      (into
       []
       (map (normalize-helper []))
       [ms/source])
      (map (normalize-helper [event-id]))
      mutant-tables)
     ::ms/references
     (let [tabv (into []
                      (map (fn [{tab "table"}] tab))
                      mutant-tables)
           pairs (partition 2 1 tabv tabv)]
       (into []
             (map (fn [[lhs rhs]]
                    [(str "has_" lhs) rhs lhs]))
             pairs))}))
