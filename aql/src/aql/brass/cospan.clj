
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

(defn provide-references [tables permute]
  [::brass-spec/references nil])

(defn target-ent->fk-mapping
  [references ent-name]
  (into {}
    (comp
     (filter (fn [[_ b _]] (= b ent-name)))
     (map (fn [[a _ _]] [a nil])))
    references))

(defn convert-perturbation
  [sample-submission-json]
  (let [tables (->> sample-submission-json
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
          (map convert-permute-entity)
          tables)
     ::brass-spec/references
     (let [tabv (into [] (into #{} (map (fn [{tab "table"}] tab)) tables))
           pairs (partition 2 1 tabv)]
       (into [["source_id" (first tabv) "source"]]
          (comp
              (map (fn [[lhs rhs]]
                     [[(str "has_" lhs) rhs lhs]
                      [(str "has_" rhs) lhs rhs]]))
              cat)
          pairs))}))

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
         sr/ALL (sr/collect-one sr/FIRST) sr/LAST sr/ALL])
       (map (fn [[arrow-type [col-name ent-name col-type]]]
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


(defn factory
  [{base ::brass-spec/s
    cospan ::brass-spec/x
    perturb ::brass-spec/schema-perturbation
    ftor-f ::brass-spec/f}]
  (let [ent-lookup (schema->col-lookup<-name base)
        perturb-lookup (perturb->col-lookup<-name perturb)
        references (::brass-spec/references perturb)
        col-lookup (merge-with #(conj %1 [::pert %2])
                               ent-lookup perturb-lookup)
        attr-lookup (filter<-type ::aql-spec/attributes col-lookup)
        ; refr-lookup (filter<-type ::aql-spec/references col-lookup)
        target-ent->col-lookup
        (->> perturb
             (sr/select [::brass-spec/tables sr/ALL])
             (map (fn [{name ::aql-spec/name, cols ::brass-spec/columns}]
                    [name cols]))
             (into {}))
        ; ent-x (->> perturb-lookup (sr/select [sr/MAP-VALS sr/FIRST]) distinct)
        ; ent-s (->> perturb-lookup (sr/select [sr/MAP-VALS sr/FIRST]) distinct)
        ent-t (->> perturb-lookup
                   (sr/select [sr/MAP-VALS sr/LAST])
                   distinct)]
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
            [col-name new-ent col-type]))
         (into []))
      :references references}

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
                      (target-ent->fk-mapping references))}]))
        (into {}))}}))
