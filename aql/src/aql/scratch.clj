

;; working with the p2 cp1 brass demo
(import '(catdata.aql AqlCmdLine))
(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))

(require '[aql.brass.data :as brass] :reload)
(require '[aql.util :as util] :reload)

(def schema brass/sc0)
(def cmd0 (util/serial-aql-schema schema))
(def cmd1 (AqlParser/parseProgram cmd0))
(def cmd2 (AqlMultiDriver. cmd1 (make-array String 1)  nil))
(.start cmd2)
(def env (.env cmd2))

(def schema1 (util/env->schema env "S"))
(util/wrap-schema schema schema1)
(util/schema->sql schema1)

(def query1 (util/env->query env "Q"))
;; (util/wrap-query query query1)
(util/query->sql query1)