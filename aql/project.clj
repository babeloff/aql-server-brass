(defproject proto-repl-driver "0.1.0-SNAPSHOT"
  :description "A Clojure project for debugging Proto REPL package for the Atom editor."
  :url "https://github.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [proto-repl "0.3.1"]
                 [proto-repl-charts "0.3.1"]]

  :profiles
  {:dev {:source-paths ["dev" "src" "test"]
         :dependencies [[org.clojure/tools.namespace "0.2.11"]]}})
