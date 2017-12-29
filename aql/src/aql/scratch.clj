

;; mucking around with Ryan's sample
(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))
    
(def cmd0 (str ts0 " " sc0 " " qu0))
(def cmd1 (AqlParser/parseProgram cmd0))
(def cmd2 (AqlMultiDriver. cmd1 (make-array String 1)  nil))
(.start cmd2)
(def cmd3 (.env cmd2))
(get (.map (.schs (.defs cmd3))) "S")
(-> cmd2 .env .defs .schs .map (get "S"))

;; working with the p2 cp1 brass demo
(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))

(require '[aql.brass.data :as brass] :reload)
(require '[aql.util :as util] :reload)

(def schema brass/sc0)
(def cmd0 (util/serial-aql-schema schema))
(def cmd1 (AqlParser/parseProgram cmd0))
(def cmd2 (AqlMultiDriver. cmd1 (make-array String 1)  nil))
(.start cmd2)
(def cmd4 (-> cmd2 .env .defs .schs .map (get "S") str))
(util/wrap-schema schema cmd4)
