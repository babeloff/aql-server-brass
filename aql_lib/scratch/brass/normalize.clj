(require '[clojure.pprint :as pp])
(require '(com.rpl [specter :as sr]))
(require '(clojure.tools [logging :as log]))

(require '[aql.spec :as aql-spec] :reload)
(require '[aql.brass.mutant :as brass-mutant] :reload)
(require '[aql.brass.spec.mutant :as brass-spec] :reload)
(require '[aql.brass.client :as brass-client] :reload)

(def permutation-json brass-client/mutant-json-live-1)

(def mutant-tables
      (sr/select-one
       ["martiServerModel"
        "requirements"
        "postgresqlPerturbation"
        "tables"]
       permutation-json))
(pp/pprint mutant-tables)

(def event-id (sr/select-one ["Event_Id"] brass-spec/lookup))

(def event-tables
       (into []
             (map (brass-mutant/normalize-helper [event-id]))
             mutant-tables))
(pp/pprint event-tables)

(def is-source? #(= % (sr/select-one ["Event_SourceId"] brass-spec/base-lookup)))

(def having-source
  (sr/select [sr/ALL
              (sr/collect-one ::aql-spec/name)
              ::brass-spec/columns sr/ALL
              is-source?]
             event-tables))
