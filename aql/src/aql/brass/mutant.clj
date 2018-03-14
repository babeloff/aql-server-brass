(ns aql.brass.mutant
  (:require
   (aql [spec :as aql-spec])
   (aql.brass.spec [mutant :as brass-spec])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])))

(defn norm-col-name
  [col-key]
  (let [{ent ::entity col ::cospan}
        (sr/select-one [col-key]
            brass-spec/lookup)]
    [ent col]))

(defn norm-entity
  [{table "table" columns "columns"}]
  {::aql-spec/name table
   ::brass-spec/columns (mapv norm-col-name columns)})

(defn normalize
  "normalize a partial json mutant object.
   it should at a minimum conform to
   aql.brass.spec.mutant/::schema-mutation
   see scratch.brass.permute.clj as an example"
  [permutation-json]
  (let [tables (->> permutation-json
                    (sr/select-one
                      ["martiServerModel"
                       "requirements"
                       "postgresqlPerturbation"
                       "tables"]))]
    {::brass-spec/tables
     (into [{::aql-spec/name "source"
             ::brass-spec/columns
                             [["source" "name"]
                              ["source" "channel"]]}]
          (map norm-entity)
          tables)
     ::brass-spec/references
     (let [tabv (into [] (into #{} (map (fn [{tab "table"}] tab)) tables))
           pairs (partition 2 1 tabv)]
       (into [["has_source" (first tabv) "source"]]
          (comp
              (map (fn [[lhs rhs]]
                     [[(str "has_" lhs) rhs lhs]
                      [(str "has_" rhs) lhs rhs]]))
              cat)
          pairs))}))
