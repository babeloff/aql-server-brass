
(ns aql.brass.cospan
  (:require
   (aql [spec :as aql-spec]
        [wrap :as aql-wrap]
        [util :as aql-util])
   (aql.brass.spec [mutant :as brass-spec])
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])
   [net.cgrand.xforms :as gxf])
  (:import
   (catdata.aql
    AqlCmdLine)
   (catdata.aql.exp
    AqlEnv
    AqlParser
    AqlMultiDriver)))

(def entity-names-xform
   (comp
    (sr/traverse-all
     [::brass-spec/tables sr/ALL
      :aql.spec/name])
    (gxf/sort-by (fn [ent-name] ent-name))))

(defn entity-map-attr-xform
  [entity-name]
  (comp
   (sr/traverse-all
     [::brass-spec/tables sr/ALL
      (sr/pred #(= (:aql.spec/name %) entity-name))
      ::brass-spec/columns sr/ALL])
   ; (gxf/sort-by (fn [[name _]] name))
   (map (fn [{col-name ::brass-spec/coname
              nu-name ::brass-spec/nuname}]
          (vector nu-name col-name)))))

(defn entity-map-ref-xform
  [entity-name]
  (comp
   (sr/traverse-all
    [::brass-spec/references sr/ALL
     (sr/pred #(= (nth % 1) entity-name))
     sr/VAL])
   (map (fn [[[a _ _]]]
          (case a
            "source_fk" [a a]
            [a nil])))))

(defn entity-map
  [mutant]
  (fn [entity-name]
    (case entity-name
      "source"
      [[[entity-name] ["source"]]
       #::aql-spec
       {:attribute-map
        (into (sorted-map)
              (entity-map-attr-xform entity-name)
              [mutant])
        :reference-map
        (into (sorted-map)
              (entity-map-ref-xform entity-name)
              [mutant])}]

      [[[entity-name] ["cospan"]]
       #::aql-spec
       {:attribute-map
        (into (sorted-map)
              (entity-map-attr-xform entity-name)
              [mutant])
        :reference-map
        (into (sorted-map)
              (entity-map-ref-xform entity-name)
              [mutant])}])))

(defn key-alias-fn [[fk tail tip]]
  (let [pk (case tail
                "source" "source_id"
                "id")
        retip (case tip
                "source" "source_id"
                "id")]
    {tail
     {::aql-wrap/pk pk
      ::aql-wrap/fk {fk retip}}}))

;;
; "cot_action"
;   #:aql.wrap
;   {:pk "id",
;    :fk {"source_fk" "source_id",
;         "cot_action_fk" "id"}}
;
;[[["x" "cot_action"]
;  [::aql-spec/equal
;   ["source_id" "x"]
;   ["source_id" ["source_fk" "x"]]
;  [::aql-spec/equal
;   ["id" "x"]
;   ["id" ["cot_action_fk" "x"]]}
;
(defn observe-fk* [tail vn pk fk]
  (let [[fref fkey] fk]
    [["x" tail]
     [::aql-spec/equal
      [fkey vn]
      [fkey [fref vn]]]]))

(defn observe-fk [[tail {pk ::aql-wrap/pk
                         fk ::aql-wrap/fk}]]
  (mapv #(observe-fk* tail "x" pk %) fk))

;; mutant example can be produced via
;; scratch/brass/mutant.clj
(defn factory
  [{base ::brass-spec/s
    cospan ::brass-spec/x
    ftor-f ::brass-spec/f
    mutant ::brass-spec/mutant}]
  (let [entity-names (sequence entity-names-xform [mutant])
        key-alias (apply aql-util/deep-merge
                    (map key-alias-fn (::brass-spec/references mutant)))]
    {::s base
     ::x cospan
     ::f ftor-f

     ::t
     #::aql-spec
     {:name "T"
      :type ::aql-spec/schema
      :extend "sql1"
      :entities
      (into #{} entity-names)

      :attributes
      ;; take mutant to this
      ;; ["cot_event_id" "cot_action" "Integer"]
      (into []
        (comp
         (sr/traverse-all
          [::brass-spec/tables sr/ALL
           (sr/collect-one :aql.spec/name)
           ::brass-spec/columns sr/ALL
           (sr/collect-one ::brass-spec/coname)
           (sr/collect-one ::brass-spec/nuname)
           ::brass-spec/type])
         (gxf/sort-by (fn [[new-ent _ nu-name _]]
                        [new-ent nu-name]))
         (map (fn [[new-ent _ nu-name col-type]]
                [nu-name new-ent col-type])))
        [mutant])

      :references
      (::brass-spec/references mutant)

      :observations
      (into [] (mapcat observe-fk) key-alias)}

     ::g
     #::aql-spec
     {:name "G"
      :type ::aql-spec/mapping
      :schema-map ["T" "X"]
      :entity-map
      (into {}
            (map (entity-map mutant))
            entity-names)}

     ::key-alias key-alias}))
