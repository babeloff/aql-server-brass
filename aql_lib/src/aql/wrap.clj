
(ns aql.wrap
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (clojure.tools [logging :as log])
   (com.rpl [specter :as sr])
   (instaparse [core :as insta]))
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


(def DUMMY_FKID "FKID")
(def DUMMY_PKID "PKID")

(defn schema->sql [name schema]
  (when schema
    (try
      (AqlCmdLine/schemaToSql schema)
      (catch Exception ex
        "cannot generate sql for schema"))))

(defn pk-alias->name
  [alias ent-alias-map]
  (fn pk-alias-lup [ent-alias]
    (let [ent-alias-str (str ent-alias)
          ent-name (get ent-alias-map ent-alias-str)]
      (log/debug "pk-alias " (str ent-alias "." ent-name) ent-alias-map)
      (get-in alias [ent-name ::pk] nil))))

(defn fk-alias->name
  "this translates fk alias names to the corresponding column"
  [alias ent-alias-map]
  (fn fk-alias-lup [fk-term]
    ;; (log/debug "fk-alias-lookup " fk-term)
    (let [ent-alias (str (.arg fk-term))
          fk-alias (str (.fk fk-term))
          ;; _ (log/debug "fk-alias " ent-alias fk-alias)
          ent-name (get ent-alias-map ent-alias)]
      (get-in alias [ent-name ::fk fk-alias] DUMMY_FKID))))

(defn quote-prime-helper
  "rewrite the referenced terms as needed"
  [fk-alias-lup term]
  (cond
    (or (.var term) (.gen term) (.sk term))
    [::term term]

    (and (.sym term) (= 0 (.size (.args term))))
    [::term term]

    (.fk term)
    [::fkref
     (Term/Fk (fk-alias-lup term)
              (second (quote-prime-helper fk-alias-lup (.arg term))))]

    (.att term)
    [::attr
     (Term/Att (.att term)
               (second (quote-prime-helper fk-alias-lup (.arg term))))]

    (.obj term)
    [::literal
     (Term/Obj (str "'" (.obj term) "'")
               (.ty term))]

    (.sym term)
    [::sym
     (Term/Sym (.sym term)
               (map #(second (quote-prime-helper fk-alias-lup %))
                    (.args term)))]

    :else [::err (Util/anomaly)]))

(defn quote-prime [fk-alias-lup term]
  (let [[k v] (quote-prime-helper fk-alias-lup term)]
      [k (.toStringSql v)]))

(defn query->sql-term-helper
  "Expand the path suitable for where clause
   Query.java : whereToString "
  [helpers ent-alias->name ctx path]
  (let [gen (.gen path)
        sk (.sk path)
        consts (.consts ctx)
        ref-alias (get helpers ::ref-alias {})
        pk-alias-lup (pk-alias->name ref-alias ent-alias->name)
        fk-alias-lup (fk-alias->name ref-alias ent-alias->name)]
    (cond
      gen [::pkid
           (str gen "." (pk-alias-lup gen))]
      sk (if (.containsKey consts sk)
           (quote-prime fk-alias-lup
                        (.convert (.get consts sk)))
           [::pvalue "?"])
      :else (quote-prime fk-alias-lup path))))

(def ob-gram (insta/parser (clojure.java.io/resource "or_bool.bnf")))

(defn rewrite-function
  [eq]
  (let [clause (insta/parses ob-gram sam-ob)]
    (if clause
      (str (get))
      eq)))

(defn query->sql-equation-helper [helpers ent-alias->name ctx eqn]
  (let [lhs (.first eqn)
        rhs (.second eqn)
        [lht lhv] (query->sql-term-helper helpers ent-alias->name ctx lhs)
        [rht rhv] (query->sql-term-helper helpers ent-alias->name ctx rhs)]
    (when (and (some? lhv) (some? rhv))
      (log/debug "where " lht " : " lhv " = " rht " : " rhv)
      ;; TODO (rewrite-function (str lhv " = " rhv)))))
      (str lhv " = " rhv))))

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
          ent-alias->name (into {}
                           (map #(vector (str %) (str (.get gens %))))
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
                 (map #(query->sql-equation-helper helpers ent-alias->name ctx %))
                 (filter some?)
                 ;; this bit filtering on DUMMY_FKID should
                 ;; likely be handled sooner.
                 (filter #(not (.contains % DUMMY_FKID))))
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
