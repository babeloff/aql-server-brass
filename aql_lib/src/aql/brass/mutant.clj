(ns aql.brass.mutant
  (:require
   (aql [spec :as aql-spec])
   (aql.brass.spec [mutant :as brass-spec])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])))

(defn normalize-helper [addendum]
  (fn [{table "table" columns "columns"}]
    {::aql-spec/name table
     ::brass-spec/columns
     (into
      addendum
      (map #(sr/select-one [%] brass-spec/lookup))
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

        event-id (sr/select-one ["Event_Id"] brass-spec/lookup)

        source-table
        (into []
              (map (normalize-helper []))
              [brass-spec/source])

        event-tables
        (into []
              (map (normalize-helper [event-id]))
              mutant-tables)

        is-source? #(= % (sr/select-one ["Event_SourceId"] brass-spec/base-lookup))

        having-source
          (sr/select-one [sr/ALL
                          (sr/collect-one ::aql-spec/name)
                          ::brass-spec/columns sr/ALL
                          is-source?]
                         event-tables)

        source-ref ["source_fk" (first having-source) "source"]

        tabv (into []
                   (map (fn [{tab "table"}] tab))
                   mutant-tables)
        pairs (partition 2 1 tabv tabv)

        references
        (into [source-ref]
              (map (fn [[lhs rhs]]
                     [(str lhs "_fk") rhs lhs]))
              pairs)]

    {::brass-spec/tables (into [] (concat source-table event-tables))
     ::brass-spec/references references}))
