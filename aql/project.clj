(defproject proto-repl-driver "0.1.0-SNAPSHOT"
  :description "A Clojure project for debugging Proto REPL package for the Atom editor."
  :url "https://github.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-tools-deps "0.1.0-SNAPSHOT"]]
  :tools/deps [:system :home :project "./deps.edn"]
  :profiles
  {:dev {:source-paths ["dev" "src" "test"]
         :tools/deps ["./deps-dev-atom.edn"]
         :dependencies [[org.clojure/tools.namespace "0.2.11"]]}})
