(ns hello-time
    (:import (catdata.aql.exp 
                AqlEnv
                AqlParser 
                AqlMultiDriver)))


(def ts0 "typeside TypeSide = literal {
    java_types
        Varchar = \"java.lang.String\"
        Bool = \"java.lang.Boolean\"
    java_constants
        Varchar = \"return input[0]\"
        Bool = \"return java.lang.Boolean.parseBoolean(input[0])\"
    java_functions
        Matches : Varchar, Varchar -> Bool = \"return input[0].matches(input[1])\"
}")

(def ts1 (AqlParser/parseProgram ts0))
(def ts2 (AqlMultiDriver. ts1 (make-array String 1)  nil))


(def cmd0 "command p3 = exec_cmdline { \"ls\" }")
(def cmd1 (AqlParser/parseProgram cmd0))
(def cmd2 (AqlMultiDriver. cmd1 (make-array String 1)  nil))
