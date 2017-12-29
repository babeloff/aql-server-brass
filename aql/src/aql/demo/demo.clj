(ns aql.demo
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

(def sc0 
    {:name "S"
     :type :schema
     :extend "sql"
     :entities 
        #{"Employee" "Department"}
     :attributes 
        {
         "first" ["Employee" "Varchar"] 
         "last" ["Employee" "Varchar"] 
         "age"  ["Employee" "Integer"] 
         "name"  ["Department" "Varchar"]}
     :references 
        {"manager" ["Employee" "Employee"]
         "worksIn"   ["Employee" "Department"]
         "secretary" ["Department" "Employee"]}
     :equations 
        [[["manager" "worksIn"] ["worksIn"]]
         [["secretary" "worksIn"] ["Department"]]
         [["manager" "manager"] ["manager"]]]})

(def qu0 "query Q = literal : S -> S {
    entity
        Employee -> 
        {
            from e:Employee d:Department
            where e.worksIn = d
            attributes 
                first -> e.manager.first 
                last -> d.name 
                age -> e.age
            foreign_keys 
                manager -> 
                    {
                        e -> e.manager
                        d -> e.manager.worksIn
                    }
                worksIn -> 
                    {d -> e.worksIn}
        }   
    entity Department -> 
        {
            from d:Department 
            attributes name -> d.name
            foreign_keys secretary -> 
                {
                    e -> d.secretary 
                    d -> d
                }
        }        
}")

(def sql0 "command export_Q = exec_js {
    \"Java.type(\\\"catdata.Util\\\").writeFile(Java.type(\\\"catdata.aql.AqlCmdLine\\\").schemaToSql(aql_env.defs.schs.get(\\\"S\\\")),\\\"/home/fred/projects/immortals/cp2/sch_test.sql\\\")\"
    \"Java.type(\\\"catdata.Util\\\").writeFile(Java.type(\\\"catdata.aql.AqlCmdLine\\\").queryToSql(aql_env.defs.qs.get(\\\"Q\\\")),\\\"/home/fred/projects/immortals/cp2/q_test.sql\\\")\"
}")

(def cmd0 (str ts0 " " sc0 " " qu0 " " sql0))
(def cmd1 (AqlParser/parseProgram cmd0))
(def cmd2 (AqlMultiDriver. cmd1 (make-array String 1)  nil))
(.start cmd2)
(def cmd3 (.env cmd2))

(defn try-parse [prog] 
    (try 
        (AqlParser/parseProgram prog)
        (catch LocException ex (.printStackTrace ex))
        (catch Throwable ex (.printStackTrace ex))))
        
(def init (try-parse prog0))
  
(def start (System/currentTimeMillis))
(def evn (make-env prog0 init))
(def middle (System/currentTimeMillis))
