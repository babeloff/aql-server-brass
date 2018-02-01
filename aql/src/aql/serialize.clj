
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

(defmulti serialize-name
  (fn [name]
    (cond
      (string? name) ::string
      (vector? name) ::vector
      :else ::default)))

(defmethod serialize-name ::string [name] name)
(defmethod serialize-name ::vector [name] (st/join "__" name))
(defmethod serialize-name ::default [name] "default")

(defn wrap-schema [schema literal]
  (str "schema " (::aql-spec/name schema)
       " = literal : " (::aql-spec/extend schema)
       " {\n" literal "\n}\n"))

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

(defmulti to-aql (fn [obj] (::aql-spec/type obj)))

(defmethod to-aql ::aql-spec/schema [schema]
  (wrap-schema
   schema
   (aql-format
    ""
    " entities "
    ::in
    (->> schema ::aql-spec/entities (map serialize-name))
    ::out
    " foreign_keys "
    ::in
    (map
     (fn [[key [src dst]]]
       (str key " : "
            (serialize-name src) " -> "
            (serialize-name dst)))
     (::aql-spec/references schema))
    ::out
    " path_equations "
    ::in
    (map
     (fn [[left right]]
       (str (st/join "." left) " = " (st/join "." right)))
     (::aql-spec/equations schema))
    ::out
    " attributes "
    ::in
    (map
     (fn [[key [src type]]]
       (str key " : "
            (serialize-name src) " -> " type))
     (::aql-spec/attributes schema)))))

(defn norm-attr->ents [schema]
  (into #{}
        (map
         (fn [[key [ent _]]]
           (str ent "__" key))
         (:attributes schema))))

(defn norm-attr->attrs [schema]
  (into {}
        (map
         (fn [[key [ent type]]]
           (let [nent (str ent "__" key)]
             [nent [nent type]]))
         (:attributes schema))))

(defn norm-refs->ents [schema])
(defn norm-attr->refs [schema])
(defn norm-refs->refs [schema])

(defn norm-aql-schema [schema]
  "Expand each attribute into its own entity.
    classes as entites are elimintated."
  wrap-schema schema
  (str
   " entities "
   (norm-attr->ents schema)
   (norm-refs->ents schema)
   " foreign_keys "
   (norm-attr->refs schema)
   (norm-refs->refs schema)
   " attributes "
   (norm-attr->attrs schema)))

(defn wrap-mapping [mapping literal]
  (str "mapping " (:name mapping)
       " = literal : " (st/join " -> " (:schemas mapping))
       " {\n" literal "\n}\n"))

(defn serialize-aql-mapping-reference
  "f -> N or f -> g"
  [[_ dest-ent] [src dest]]
  (cond
    (nil? dest)
    (str src " -> " dest-ent)

    :else
    (str src " -> " dest)))

(defn serialize-aql-mapping-attribute
  "f -> N or f -> g"
  [[_ _] [src dest]]
  (str src " -> " dest))

(defn serialize-aql-mapping-entity
  [[entity-key entity-value]]
  (let [[src dest] (map serialize-name entity-key)
        fks (:references entity-value)
        attrs (:attributes entity-value)]
    (aql-format
     ""
     " entity "
     ::in
     (str src " -> " dest)
     ::out
     " foreign_keys "
     ::in
     (map #(serialize-aql-mapping-reference [src dest] %) fks)
     ::out
     " attributes "
     ::in
     (map #(serialize-aql-mapping-attribute [src dest] %) attrs))))

(defmethod to-aql ::aql-spec/mapping 
  [mapping]
  (->>
   (:entities mapping)
   (map serialize-aql-mapping-entity)
   (st/join "\n")
   (wrap-mapping mapping)))
