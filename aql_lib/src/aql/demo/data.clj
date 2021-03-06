(ns aql.demo.data
  (:require (aql [spec :as s])))

(def schema-s
  #::s
  {:name "S"
   :type ::s/schema
   :extend "sql"
   :entities
   #{"Employee" "Department"}
   :attributes
   [["first" "Employee" "Varchar"]
    ["last" "Employee" "Varchar"]
    ["age"  "Employee" "Integer"]
    ["name"  "Department" "Varchar"]]
   :references
   [["manager" "Employee" "Employee"]
    ["worksIn"  "Employee" "Department"]
    ["secretary" "Department" "Employee"]]
   :observations
   [[["x" "Employee"]
     [::s/equal
      ["worksIn" ["manager" "x"]] ["worksIn" "x"]]
     [::s/equal
      ["secretary" "worksIn" "x"] ["Department" "x"]]
     [::s/equal ["manager" "manager" "x"] ["manager" "x"]]]]})

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
    entity
        Department ->
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
