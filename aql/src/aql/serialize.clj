
(ns aql.serialize
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (clojure.tools [logging :as log])
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
  (let [indent (atom base-indent)]
    (letfn [(indentter [ax]
              (log/debug "indent")
              (swap! indent #(str "  " %))
              ax)
            (outdentter [ax]
                        (log/debug "outdent")
                        (swap! indent #(subs % (min (count %) 2)))
                        ax)
            (resetter [ax]
                      (log/debug "reset")
                      (reset! indent base-indent) ax)
            (symboller [ax obj]
                       (log/error "serialize a function? " obj)
                       ax)
            (stringger [ax obj]
                       (log/debug "string" obj)
                       (str ax "\n" @indent obj))
            (helper
             [ax obj]
             (try
               (cond
                 (nil? obj) ax
                 (symbol? obj) (symboller ax obj)
                 (and (keyword? obj) (= ::in obj)) (indentter ax)
                 (and (keyword? obj) (= ::out obj)) (outdentter ax)
                 (and (keyword? obj) (= ::reset obj)) (resetter ax)
                 (string? obj) (stringger ax obj)
                 (seq obj) (do (log/debug "helper" obj)
                             (reduce helper ax obj))
                 :else (stringger ax obj))
               (catch Throwable ex
                 (log/error  "problem formatting: "
                             (pr-str (take 10 obj))
                             ex))))]
      (reduce helper "" coll))))

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
  (log/info "to-literal schema " schema)
  (aql-format
   "  "
   (when-let [entities (::aql-spec/entities schema)]
     ["entities "
      ::in
      (map to-name entities)
      ::out])
   (comment)
   (when-let [references (::aql-spec/references schema)]
     ["foreign_keys "
      ::in
      (map
       (fn [[key [src dst]]]
         (str key " : "
              (to-name src) " -> "
              (to-name dst)))
       references)
      ::out])
   (comment)
   (when-let [equations (::aql-spec/equations schema)]
     ["path_equations "
      ::in
      (map
       (fn [[left right]]
         (str (st/join "." left) " = " (st/join "." right)))
       equations)
      ::out])
   (comment)
   (when-let [attributes (::aql-spec/attributes schema)]
     ["attributes "
      ::in
      (map
       (fn [[key [src type]]]
         (str key " : "
              (to-name src) " -> " type))
       attributes)])))

(defmethod wrap-literal
  ::aql-spec/mapping
  [mapping literal]
  (str "mapping " (::aql-spec/name mapping)
       " = literal : " (st/join " -> " (::aql-spec/schema-map mapping))
       " {" literal "\n}\n"))

(defn to-literal-mapping-entity
  [[entity-key entity-value]]
  (let [[to-src-ent to-dest-ent] (map to-name entity-key)]
    (aql-format
     "  "
     (comment)
     (str "entity " (str to-src-ent " -> " to-dest-ent))
     ::in
     (comment)
     (when-let [fks (::aql-spec/reference-map entity-value)]
       (when fks
         ["foreign_keys "
          ::in
          (map
           (fn [[from-src-ent from-dest-ent]]
             (cond
               (nil? from-dest-ent)
               (str from-src-ent " -> " to-dest-ent)

               :else
               (str from-src-ent " -> " from-dest-ent)))
           fks)
          ::out]))
     (comment)
     (when-let [attrs (::aql-spec/attribute-map entity-value)]
       (when attrs
         ["attributes "
          ::in
          (map
           (fn [[from-src from-dest]]
             (str from-src " -> " from-dest))
           attrs)
          ::out])))))

(defmethod to-aql
  ::aql-spec/mapping
  [mapping]
  (wrap-literal mapping (to-literal mapping)))

(defmethod to-literal
  ::aql-spec/mapping
  [mapping]
  (->> mapping
       ::aql-spec/entity-map
       (map to-literal-mapping-entity)
       st/join))
