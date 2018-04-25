#!/usr/bin/env boot

(def group-id "brass-immortals")
(def artifact-id "aql-brass-server")
(def version "2018.04.18")
(def main-entry 'aql.brass.server)
(def exec-jar (str artifact-id "-" version ".jar"))

(set-env!
  :source-paths #{"src"}
  :repositories {"central" "http://repo1.maven.org/maven2/"
                 "clojars" "https://clojars.org/repo/"
                 "nexus" "http://immortals.isis.vanderbilt.edu:8081/repository/maven-snapshots"}
  :dependencies
    '[[org.clojure/clojure "1.9.0"]
      [http-kit "2.2.0"]
      [javax.xml.bind/jaxb-api "2.3.0"]
      [com.rpl/specter  "1.1.0"]
      [net.catdata/fql "0.9-SNAPSHOT"]
      [org.clojure/data.json "0.2.6"]
      [org.clojure/tools.logging "0.2.3"]
      [ch.qos.logback/logback-classic "1.0.1"]
      [compojure "1.6.0"]
      [ring/ring-json "0.4.0"]
      [javax.servlet/servlet-api "2.5"]
      [commons-daemon/commons-daemon "1.1.0"]
      [org.clojure/tools.nrepl "0.2.12"]
      [org.clojure/tools.cli "0.3.5"]
      [net.cgrand/xforms "0.16.0"]])
    ;; '[[seancorfield/boot-tools-deps "0.4.3"]]

;; (require '[boot-tools-deps.core :refer [deps]]))

(task-options!
  pom {:project     (str group-id artifact-id)
       :version     version
       :description "A server for running AQL programs"
       :url         "https://github.com/babeloff/aql-server-brass/wiki"
       :scm         {:url "https://github.com/babeloff/aql-server-brass"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}}

  aot {:namespace #{main-entry}}
  jar {:file exec-jar :main main-entry}
  install {}
  sift {} ;:include #{#"jar$"}}
       ;; :move "(.*\\.jar$):WEB-INF/lib/$1")}
  push {}
  uber {}
  target {:dir #{"target"}})

(deftask uberjar
  [] (comp (aot) (uber) (jar) (sift :include #{(re-pattern exec-jar)}) (target)))

;(require '[aql.brass.server :as abs])
;(defn -main [& args]
;  (abs/-main args))
