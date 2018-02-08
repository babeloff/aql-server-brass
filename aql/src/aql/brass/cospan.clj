
(ns aql.brass.cospan
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

(defn perturb->col-lookup<-name
  "construct an sequence of tuples [new-entity old-entity column]"
  [pert]
  (->> pert
       (sr/select
        [::brass-spec/tables sr/ALL (sr/collect-one ::aql-spec/name)
         ::brass-spec/columns sr/ALL (sr/collect-one sr/FIRST)
         sr/LAST])
       (map (fn [[new-ent old-ent col-name]]
              [col-name [old-ent new-ent]]))
       (into {})))

(defn schema->col-lookup<-name
  [base]
  (->> base
       (sr/select
        [(sr/submap [::aql-spec/references ::aql-spec/attributes])
         sr/ALL (sr/collect-one sr/FIRST)
         sr/LAST sr/ALL (sr/collect-one sr/FIRST)
         sr/LAST (sr/collect-one sr/FIRST)
         sr/LAST])
       (map (fn [[arrow-type col-name ent-name col-type]]
              [col-name {::atype arrow-type
                         ::ent-name ent-name
                         ::col-ent col-type}]))
       (into {})))

(defn filter<-type
  [type col-lookup]
  (sr/setval
   [sr/MAP-VALS
    (sr/not-selected?
      ::atype
      (sr/pred= type))]
   sr/NONE col-lookup))

(defn target-ent->fk []
  {"source_id" ["cot_action" "source"]
   "has_cot_action" ["cot_detail" "cot_action"]
   "has_cot_detail" ["cot_action" "cot_detail"]})

(defn target-ent->fk-mapping [ent-name]
   (get {"source" nil
         "cot_action" {"source_id" nil}
         "cot_detail" {"has_cot_action" nil}}
        ent-name))

(defn factory
  [{base ::brass-spec/s
    cospan ::brass-spec/x
    pert ::brass-spec/schema-perturbation
    ftor-f ::brass-spec/f}]
  (let [ent-lookup (schema->col-lookup<-name base)
        perturb-lookup (perturb->col-lookup<-name pert)
        col-lookup (merge-with #(conj %1 [::pert %2])
                               ent-lookup perturb-lookup)
        attr-lookup (filter<-type ::aql-spec/attributes col-lookup)
        ; refr-lookup (filter<-type ::aql-spec/references col-lookup)
        target-ent->col-lookup
        (->> pert
             (sr/select [::brass-spec/tables sr/ALL])
             (map (fn [{name ::aql-spec/name, cols ::brass-spec/columns}]
                    [name cols]))
             (into {}))
        ; ent-x (->> perturb-lookup (sr/select [sr/MAP-VALS sr/FIRST]) distinct)
        ; ent-s (->> perturb-lookup (sr/select [sr/MAP-VALS sr/FIRST]) distinct)
        ent-t (->> perturb-lookup (sr/select [sr/MAP-VALS sr/LAST]) distinct)]
    {::s base
     ::x cospan
     ::f ftor-f

     ::t
     #::aql-spec
     {:name "T"
      :type ::aql-spec/schema
      :extend "sql1"
      :entities (into #{} ent-t)
      :attributes
      (->> attr-lookup
         (map
          (fn [[col-name {[_ new-ent] ::pert, col-type ::col-ent}]]
            [col-name [new-ent col-type]]))
         (into {}))
      :references
      (target-ent->fk)}

     ::g
     #::aql-spec
     {:name "G"
      :type ::aql-spec/mapping
      :schema-map ["T" "X"]
      :entity-map
      (->> ent-t
        (map (fn [ent-name]
               [[[ent-name] ["cot_cospan"]]
                #::aql-spec
                {:attribute-map
                 (->> ent-name
                  target-ent->col-lookup
                  (map (fn [[_ col-name]] (vector col-name col-name)))
                  (into {}))
                 :reference-map
                 (->> ent-name
                      target-ent->fk-mapping)}]))
        (into {}))}}))
