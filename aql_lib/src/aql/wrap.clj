
(ns aql.wrap
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr]))
  (:import
   (catdata
    LineException
    Util)
   (catdata.aql
    AqlCmdLine
    Term)
   (catdata.aql.exp
    AqlEnv
    AqlParser
    AqlMultiDriver)))

(defn schema->sql [name schema]
  (when schema
    (try
      (AqlCmdLine/schemaToSql schema)
      (catch Exception ex
        "cannot generate sql for schema"))))

(defn quote-prime-helper
  "rewrite the referenced terms as needed"
  [ref-alias-fn term]
  (cond
    (or (.var term) (.gen term) (.sk term))
    [::term term]

    (and (.sym term) (= 0 (.size (.args term))))
    [::term term]

    (.fk term)
    [::fkref
     (Term/Fk (.fk term)
              (second (quote-prime-helper ref-alias-fn (.arg term))))]

    (.att term)
    [::attr
     (Term/Att (.att term)
               (second (quote-prime-helper ref-alias-fn (.arg term))))]

    (.obj term)
    [::literal
     (Term/Obj (str "'" (.obj term) "'")
               (.ty term))]

    (.sym term)
    [::sym
     (Term/Sym (.sym term)
               (map #(second (quote-prime-helper ref-alias-fn %))
                    (.args term)))]

    :else [::err (Util/anomaly)]))

(defn quote-prime [ref-alias-fn term]
  (let [[k v] (quote-prime-helper ref-alias-fn term)]
      [k (.toStringSql v)]))

(defn query->sql-term-helper
  "Expand the path suitable for where clause
   Query.java : whereToString "
  [helpers from-alias ctx path]
  (let [gen (.gen path)
        sk (.sk path)
        consts (.consts ctx)
        ref-alias-fn (get-in helpers [::ref-alias-fn] identity)]
    (cond
      gen [::pkid
           (str gen "."
             (ref-alias-fn from-alias ::pk gen))]
      sk (if (.containsKey consts sk)
           (quote-prime ref-alias-fn
                        (.convert (.get consts sk)))
           [::pvalue "?"])
      :else (quote-prime ref-alias-fn path))))

(defn query->sql-equation-helper [helpers from-alias ctx eqn]
  (let [lhs (.first eqn)
        rhs (.second eqn)
        [lht lhv] (query->sql-term-helper helpers from-alias ctx lhs)
        [rht rhv] (query->sql-term-helper helpers from-alias ctx rhs)]
    (when (and (some? lhv) (some? rhv))
      (str lht ":" lhv " = " rht ":" rhv))))

(defn query->sql-ent-helper
  "Expand the query entity [there may be more than one].
   see fql :: src/catdata/aql/Query.java :: toSQLViews()"
  [query-name helpers ctx schema ents ent-key attrs refs]
  (let [b (.get ents ent-key)
        gens (.gens b)]
    (if (.isEmpty gens)
      (throw (RuntimeException. "empty from clause invalid sql")))
    (let [eqns (.eqs b)
          sort-select-fn (get-in helpers [::sort-select-fn] identity)
          from-alias (into {}
                           (map #(vector % (.get gens %)))
                           (.keySet gens))

          from
          (into []
                (map #(str (.get gens %) " as " %))
                (.keySet gens))

          select-attr
          (into []
                (comp
                 (map (fn [attr] (str (.get attrs attr) " as " attr))))
                (sort-select-fn query-name
                                (.attsFrom schema ent-key)))

          select-ref
          (into []
                (map (fn [ref] (str (.get refs ref) " as " ref)))
                (.fksFrom schema ent-key))

          where
          (into []
                (comp
                 (map #(query->sql-equation-helper helpers from-alias ctx %))
                 (filter some?))
                eqns)]

      ;; skip ID column (.add select (.)))))
      (str " select " (st/join ", " select-attr)
           "\n"
           " from " (st/join ", " from)
           "\n"
           (if (empty? where) ";"
               (str " where " (st/join " and " where) "\n;"))))))

(defn query->sql-helper
  "Expand the query .
   see fql :: src/catdata/aql/Query.java"
  [query-name helpers full-query]
  (let [schema (.dst full-query)
        ents (.ens full-query)
        atts (.atts full-query)
        refs (.fks full-query)
        ent-keys (.keySet ents)]
    (into {}
          (map
           (fn [ent-key]
             (vector
              ent-key
              (query->sql-ent-helper
               query-name
               helpers full-query schema
               ents ent-key atts refs))))
          ent-keys)))

(defn query->sql
  "a modified version of catdata.aql.AqlCmdLine/queryToSql
   * does not include 'create view'
   * replaces whitespace characters with blanks."
  [name helpers query]
  (when query
    (try
      (let [full-query (.unnest query)
            qs (query->sql-helper name helpers full-query)
            qm (.ens (.dst query))]
        (-> (reduce #(str %1 (.get qs %2) "  ") "" qm)
            (st/replace #"[\n\t\r]" " ")
            st/trim))
      (catch Exception ex
        (log/error "cannot generate sql for query" ex)))))

(defn env->maps [env]
  (let [env-defs (.-defs env)]
    {::instance (-> env-defs .-qs .-map)
     ::mapping (-> env-defs .-maps .-map)
     ::schema (-> env-defs .-schs .-map)
     ::transform (-> env-defs .-trans .-map)
     ::typeside (-> env-defs .-tys .-map)
     ::query (-> env-defs .-qs .-map)
     ::command (-> env-defs .-ps .-map)
     ::graph (-> env-defs .-gs .-map)
     ::comment (-> env-defs .-cs .-map)
     ::schema-colimit (-> env-defs .-scs .-map)
     ::constraint (-> env-defs .-eds .-map)}))

(sr/declarepath IS-QUERY)
(sr/providepath IS-QUERY
                (sr/cond-path
                 (sr/must "query") "query"
                 (sr/must :query) :query))

(sr/declarepath IS-SCHEMA)
(sr/providepath IS-SCHEMA
                (sr/cond-path
                 (sr/must "schema") "schema"
                 (sr/must :schema) :schema))

(sr/declarepath IS-ERR)
(sr/providepath IS-ERR
                (sr/cond-path
                 (sr/must "err") "err"
                 (sr/must :err) :err))

(defn xform-result
  "the tweeker is an optional transducer that gets
  applied to the result immediately before being
  placed into the vector. "
  [helpers reqs gen]
  (log/debug "extract-result" reqs)
  (let [env-map (env->maps (sr/select-one [:env] gen))
        query-fn
        (fn [name] (query->sql name helpers (get (::query env-map) name)))

        schema-fn
        (fn [name] (schema->sql name (get (::schema env-map) name)))]
    {:query
     (into {}
           (comp
            (map #(vector % (query-fn %)))
            (get-in helpers [::tweek-output-xf] identity))
           (sr/select-one [IS-QUERY] reqs))
     :schema
     (into []
           (map #(vector % (schema-fn %)))
           (sr/select-one [IS-SCHEMA] reqs))
     :error
     (into []
           (map #(.getMessage %))
           (sr/select-one [IS-ERR] gen))}))

(defn private-field
  [field-name obj]
  (doto (-> obj (.getClass) (.getDeclaredField field-name))
    (.setAccessible true)
    (.get obj)))

(defn make-driver [model]
  (-> (AqlParser/getParser)
      (.parseProgram model)
      (AqlMultiDriver. (make-array String 1)  nil)))

(defn generate [model]
  (let [driver (make-driver model)]
    (.start driver)
    {:status (.toString driver)
     :env (.-env driver)
     :err (->> (.exn driver)
               (filter #(instance? LineException %))
               (into []))}))
