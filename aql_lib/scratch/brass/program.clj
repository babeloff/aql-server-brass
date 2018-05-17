
(require
 '(clojure.data [json :as json])
 '(clojure.tools [logging :as log])
 '(clojure [pprint :as pp]
           [string :as st])
 '(com.rpl [specter :as sr])
 '(aql [wrap :as aql-wrap]
       [util :as aql-util]))
;; working with the p2 cp1 brass demo
;; (require '[aql.serialize :as ser] :reload)

(import
 '(catdata.aql.exp
   AqlEnv
   AqlParser
   AqlMultiDriver)
 '(catdata LineException))

(def model (slurp "brass_data.aql"))

(def parser (AqlParser/getParser))
(def prog (.parseProgram parser model))
