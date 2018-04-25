(defproject proto-repl-driver "0.1.0-SNAPSHOT"
  :description "A building the aql-brass-server."
  :url "https://github.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-tools-deps "0.1.0-SNAPSHOT"]]
  :tools/deps [:project]
  :profiles
  {:dev {:source-paths ["dev" "src" "test"]
         :dependencies [[org.clojure/tools.namespace "0.2.11"]]}}
  :main aql.brass.server
  :aot [aql.brass.server])
