
(ns aql.brass.util
  (:require
   (aql [spec :as aql-spec])
   (aql.brass [spec :as brass-spec])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr]))
  (:import
   (catdata.aql
    AqlCmdLine)
   (catdata.aql.exp
    AqlEnv
    AqlParser
    AqlMultiDriver)))


(defn- convert-permute-col-name
  [col-key]
  (let [{ent :ent col :col}
        (sr/select-one [col-key]
            brass-spec/schema-permutation-mapping)]
    [ent col]))

(defn- convert-permute-entity
  [{table "table" columns "columns"}]
  {::aql-spec/name table
   ::brass-spec/columns (mapv convert-permute-col-name columns)})

(defn convert-perturbation
  [sample-submission-json]
  (let [init-source [{::aql-spec/name "source"
                      ::brass-spec/columns
                      [["source" "name"]
                       ["source" "channel"]]}]]
    {::brass-spec/tables
     (->>
       sample-submission-json
       (sr/select-one
         ["martiServerModel"
          "requirements"
          "postgresqlPerturbation"
          "tables"])
       (map convert-permute-entity)
       (into init-source))}))

(defn- expand-perturbation
  "construct an sequence of tuples [new-entity old-entity column]"
  [pert]
  (->> pert
       (sr/select [:tables sr/ALL (sr/collect-one :name)
                   :columns sr/ALL (sr/collect-one sr/FIRST)
                   sr/LAST])
       (map (fn [[new-ent old-ent col-name]]
              [col-name [old-ent new-ent]]))
       (into {})))

(defn- schema-map-by-name
  [base]
  (->> base
       (sr/select
        [(sr/submap [:references :attributes]) sr/ALL (sr/collect-one sr/FIRST)
         sr/LAST sr/ALL (sr/collect-one sr/FIRST)
         sr/LAST (sr/collect-one sr/FIRST)
         sr/LAST])
       (map (fn [[arrow-type col-name ent-name col-type]]
              [col-name {:atype arrow-type
                         :ent-name ent-name
                         :col-type col-type}]))
       (into {})))

(defn aql-cospan-factory
  [base pert]
  (let [ent-map (schema-map-by-name base)
        arrows (expand-perturbation pert)
        col-map (merge-with #(conj %1 [:move %2]) ent-map arrows)
        ent-x (->> arrows (sr/select [sr/MAP-VALS]) distinct)
        ; ent-s (->> arrows (sr/select [sr/MAP-VALS sr/FIRST]) distinct)
        ent-t (->> arrows (sr/select [sr/MAP-VALS sr/LAST]) distinct)]
    {::s base

     ::x
     #::aql-spec
     {:name "X"
      :type :schema
      :extend "sql1"
      :entities #{"cot_cospan"}
      :attributes
      (->> col-map
           (filter (fn [[_ {atype :atype}]] (= atype :attributes)))
           (map
            (fn [[col-name {col-type :col-type}]]
              [col-name ["cot_cospan" col-type]]))
           (into {}))}
     ::t
     #::aql-spec
     {:name "T"
      :type :schema
      :extend "sql1"
      :entities (into #{} ent-t)
      :attributes
      (->> col-map
           (filter (fn [[_ {atype :atype}]] (= atype :attributes)))
           (map
            (fn [[col-name {[_ new-ent] :move, col-type :col-type}]]
              [col-name [new-ent col-type]]))
           (into {}))
      :references
      (->> col-map
           (filter (fn [[_ {atype :atype}]] (= atype :references)))
           (map
            (fn [[col-name {[_ new-ent] :move, col-type :col-type}]]
              [col-name [new-ent col-type]]))
           (into {}))}
     ::f
     #::aql-spec
     {:name "F"
      :type :mapping
      :schemas ["X" "S"]
      :entities (-> ent-x identity)
      :attributes nil
      :references nil}
     ::g
     #::aql-spec
     {:name "G"
      :type :mapping
      :schemas ["X" "T"]
      :entities (-> ent-x identity)
      :attributes nil
      :references nil}}))
