
(ns aql.serialize
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])
   (aql [spec :as aql-spec])))

(defn env->schema [env name]
  (-> env .defs .schs .map (get name)))

(defn env->schema-names [env] (-> env .defs .schs .map keys))

(defn env->query [env name]
  (-> env .defs .qs .map (get name)))

(defn env->query-names [env] (-> env .defs .qs .map keys))

(defmulti to-name
  (fn [name]
    (cond
      (string? name) ::string
      (vector? name) ::vector
      :else ::default)))

(defmulti wrap-literal (fn [obj literal] (::aql-spec/type obj)))
(defmulti to-literal (fn [obj] (::aql-spec/type obj)))
(defmulti to-aql
  (fn [obj]
    (cond
      (string? obj) ::string
      (contains? obj ::aql-spec/type) (::aql-spec/type obj)
      :else ::defult)))

(defmethod to-aql ::string [obj] obj)
(defmethod to-aql ::default [obj] (str "// no " obj))

(defmethod to-name ::string [name] name)
(defmethod to-name ::vector [name] (st/join "__" name))
(defmethod to-name ::default [name] "default")

(defn aql-format [base-indent & coll]
  (let [indent (atom base-indent)
        helper
        (fn [ax val]
          (cond
            (= ::in val)
            (do
              (swap! indent #(str "  " %))
              ax)
            (= ::out val)
            (do
              (swap! indent #(subs % (min (count %) 2)))
              ax)
            (= ::reset val)
            (do
              (reset! indent base-indent)
              ax)
            (coll? val)
            (do
              (reduce #(str %1 "\n" @indent %2) ax val))
            :else
            (do
              (str ax "\n" @indent val))))]
    (reduce helper "" coll)))

(defmethod wrap-literal
  ::aql-spec/schema
  [schema literal]
  (str "schema " (::aql-spec/name schema)
       " = literal : " (::aql-spec/extend schema)
       " {" literal "\n}\n"))

(defmethod to-aql
  ::aql-spec/schema
  [schema]
  (wrap-literal schema (to-literal schema)))

(defmethod to-literal
  ::aql-spec/schema
  [schema]
  (aql-format
   "  "
   "entities "
   ::in
   (->> schema ::aql-spec/entities (map to-name))
   ::out
   "foreign_keys "
   ::in
   (map
    (fn [[key [src dst]]]
      (str key " : "
           (to-name src) " -> "
           (to-name dst)))
    (::aql-spec/references schema))
   ::out
   "path_equations "
   ::in
   (map
    (fn [[left right]]
      (str (st/join "." left) " = " (st/join "." right)))
    (::aql-spec/equations schema))
   ::out
   "attributes "
   ::in
   (map
    (fn [[key [src type]]]
      (str key " : "
           (to-name src) " -> " type))
    (::aql-spec/attributes schema))))


(defmethod wrap-literal
  ::aql-spec/mapping
  [mapping literal]
  (str "mapping " (::aql-spec/name mapping)
       " = literal : " (st/join " -> " (::aql-spec/schema-map mapping))
       " {" literal "\n}\n"))

(defn to-aql-mapping-reference
  "f -> N or f -> g"
  [[_ dest-ent] [src dest]]
  (cond
    (nil? dest)
    (str src " -> " dest-ent)

    :else
    (str src " -> " dest)))

(defn to-aql-mapping-attribute
  "f -> N or f -> g"
  [[_ _] [src dest]]
  (str src " -> " dest))

(defn to-literal-mapping-entity
  [[entity-key entity-value]]
  (let [[src dest] (map to-name entity-key)
        fks (::aql-spec/reference-map entity-value)
        attrs (::aql-spec/attribute-map entity-value)]
    (aql-format
     "  "
     (str "entity " (str src " -> " dest))
     ::in
     "foreign_keys "
     ::in
     (map #(to-aql-mapping-reference [src dest] %) fks)
     ::out
     "attributes "
     ::in
     (map #(to-aql-mapping-attribute [src dest] %) attrs))))

(defmethod to-aql
  ::aql-spec/mapping
  [mapping]
  (wrap-literal mapping (to-literal mapping)))

(defmethod to-literal
  ::aql-spec/mapping
  [mapping]
  (->>
   (::aql-spec/entity-map mapping)
   (map to-literal-mapping-entity)
   st/join))
