#!/usr/bin/env boot

(def group-id "brass-immortals")
(def artifact-id "aql-brass-server")
(def version "2018.04.18")
(def main-entry 'aql.brass.daemon)
(def exec-jar (str artifact-id "-" version ".jar"))

(set-env!
  :source-paths #{"src"}
  :repositories {"central" "http://repo1.maven.org/maven2/"
                 "clojars" "https://clojars.org/repo/"
                 "nexus" "https://nexus.isis.vanderbilt.edu/repository/maven-snapshots"}
  :dependencies
    '[[seancorfield/boot-tools-deps RELEASE]])

(require '[boot-tools-deps.core :refer [deps]])

(task-options!
  pom {:project     (symbol (str group-id "/" artifact-id))
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

(deftask build
  [] (comp (deps) (aot) (pom) (uber) (jar) (sift :include #{(re-pattern exec-jar)}) (target)))

(deftask uberjar
  [] (comp (deps) (aot) (uber) (jar) (sift :include #{(re-pattern exec-jar)}) (target)))

;(require '[aql.brass.server :as abs])
;(defn -main [& args]
;  (abs/-main args))
