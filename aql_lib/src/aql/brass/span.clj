(ns aql.brass.span
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])
   (aql [spec :as aql-spec])))


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
    (str
     " entities "
     (norm-attr->ents schema)
     (norm-refs->ents schema)
     " foreign_keys "
     (norm-attr->refs schema)
     (norm-refs->refs schema)
     " attributes "
     (norm-attr->attrs schema)))
