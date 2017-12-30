

;; mucking around with Ryan's sample
(import '(catdata.aql.exp AqlEnv AqlParser AqlMultiDriver))
(import '(catdata.aql AqlCmdLine))
(require '[aql.demo.data :as demo] :reload)
(require '[aql.util :as util] :reload) 

(def cmd0 
    (str demo/ts0 "\n" 
        (util/serial-aql-schema demo/sc0) "\n" 
        demo/qu0))
(def cmd1 (AqlParser/parseProgram cmd0))
(def cmd2 (AqlMultiDriver. cmd1 (make-array String 1)  nil))
(.start cmd2)
;; <fql>/src/catdata/aql/exp/KindCtx.java
(def env (-> cmd2 .env)) 
    
(def schema1 (util/env->schema env "S"))
(util/wrap-schema schema schema1)
(util/schema->sql schema1)

(def query1 (util/env->query env "Q"))
;; (util/wrap-query query query1)
(util/query->sql query1)

(defn try-parse [prog] 
    (try 
        (AqlParser/parseProgram prog)
        (catch LocException ex (.printStackTrace ex))
        (catch Throwable ex (.printStackTrace ex))))
        
;; (def init (try-parse prog0))
  
;;(def start (System/currentTimeMillis))
;;(def evn (make-env prog0 init))
;;(def middle (System/currentTimeMillis))