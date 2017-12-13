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

(def sc0 "schema S = literal : sql {
	entities
		Employee 
		Department
	foreign_keys
		manager   : Employee -> Employee
		worksIn   : Employee -> Department
		secretary : Department -> Employee
	path_equations 
		manager.worksIn = worksIn
  		secretary.worksIn = Department
  		manager.manager = manager
  	attributes
  		first last	: Employee -> Varchar
     	age			: Employee -> Integer
     	name 		: Department -> Varchar
 }")

(def qu0 "query Q = literal : S -> S {
	entity
		Employee -> 
		{from e:Employee d:Department
		 where e.worksIn = d
		 attributes first -> e.manager.first 
		        last -> d.name 
		        age -> e.age
		        foreign_keys manager -> {e -> e.manager
		            d -> e.manager.worksIn}
		worksIn -> {d -> e.worksIn}
		}
		
		entity Department -> {from d:Department 
		               attributes name -> d.name
		               foreign_keys secretary -> {e -> d.secretary 
		              d -> d}}		
}")

(def sql0 "command export_Q = exec_js {
	\"Java.type(\\\"catdata.Util\\\").writeFile(Java.type(\\\"catdata.aql.AqlCmdLine\\\").schemaToSql(aql_env.defs.schs.get(\\\"S\\\")),\\\"/Users/ryan/Desktop/sch_test.sql\\\")\"
	\"Java.type(\\\"catdata.Util\\\").writeFile(Java.type(\\\"catdata.aql.AqlCmdLine\\\").queryToSql(aql_env.defs.qs.get(\\\"Q\\\")),\\\"/Users/ryan/Desktop/q_test.sql\\\")\"
}")


(def ts1 (AqlParser/parseProgram ts0))
(def ts2 (AqlMultiDriver. ts1 (make-array String 1)  nil))


(def cmd0 "command p3 = exec_cmdline { \"ls\" }")
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
