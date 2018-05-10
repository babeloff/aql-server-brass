
(ns aql.brass.cospan
  (:require
   (aql [spec :as aql-spec])
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
   (map (fn [{col-name ::brass-spec/coname}]
          (vector col-name col-name)))))

(defn entity-map-ref-xform
  [entity-name]
  (comp
   (sr/traverse-all
    [::brass-spec/references sr/ALL
     (sr/pred #(= (nth % 1) entity-name))
     sr/VAL])
   (map (fn [[[a _ _]]] [a nil]))))

(defn entity-map
  [mutant]
  (fn [entity-name]
    [[[entity-name] ["cospan"]]
     #::aql-spec
     {:attribute-map
      (into (sorted-map)
            (entity-map-attr-xform entity-name)
            [mutant])
      :reference-map
      (into (sorted-map)
            (entity-map-ref-xform entity-name)
            [mutant])}]))

;; mutant example can be produced via
;; scratch/brass/mutant.clj
(defn factory
  [{base ::brass-spec/s
    cospan ::brass-spec/x
    ftor-f ::brass-spec/f
    mutant ::brass-spec/mutant}]
  {::s base
   ::x cospan
   ::f ftor-f

   ::t
   #::aql-spec
   {:name "T"
    :type ::aql-spec/schema
    :extend "sql1"
    :entities
    (into #{} entity-names-xform [mutant])

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
         ::brass-spec/type])
       (gxf/sort-by (fn [[new-ent col-name _]] [new-ent col-name]))
       (map (fn [[new-ent col-name col-type]] [col-name new-ent col-type])))
      [mutant])

    :references
    (::brass-spec/references mutant)

    :observations nil}
    ;[[["x" "cot_action"]
    ;  [::aql-spec/equal
    ;   ["source_id" "x"]
    ;   ["id" ["has_source" "x"]]
    ; [["y" "cot_detail"]
    ;  [::aql-spec/equal
    ;   ["cot_event_id" "y"]
    ;   ["id" ["has_cot_action" "y"]]}

   ::g
   #::aql-spec
   {:name "G"
    :type ::aql-spec/mapping
    :schema-map ["T" "X"]
    :entity-map
    (into {}
      (comp
        entity-names-xform
        (map (entity-map mutant)))
     [mutant])}})
