(ns boot.user
    (:require (boot [core :as bc]
                    [util :as bu])
              (boot.task [built-in :as bi])))

(def group-id "brass-immortals")
(def artifact-id "aql-brass-server")
(def version "2018.04.18")
(def main-entry 'aql.brass.daemon)
(def exec-jar (str artifact-id "-" version ".jar"))

(bc/set-env!
  :source-paths #{"src"}
  :repositories {"central" "http://repo1.maven.org/maven2/"
                 "clojars" "https://clojars.org/repo/"
                 "nexus" "https//nexus.isis.vanderbilt.edu/repository/maven-snapshots"}
  :dependencies
    '[[seancorfield/boot-tools-deps RELEASE]])

(require '[boot-tools-deps.core :as bt])

(bc/task-options!
  bi/pom {:project     (symbol (str group-id "/" artifact-id))}
       :version     version
       :description "A server for running AQL programs"
       :url         "https://github.com/babeloff/aql-server-brass/wiki"
       :scm         {:url "https://github.com/babeloff/aql-server-brass"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}

  bi/aot {:namespace #{main-entry}}
  bi/jar {:file exec-jar :main main-entry}
  bi/install {}
  bi/sift {} ;:include #{#"jar$"}}
       ;; :move "(.*\\.jar$):WEB-INF/lib/$1")}
  bi/push {}
  bi/uber {}
  bi/target {:dir #{"target"}})

(bc/deftask build
  [] (comp (bt/deps) (bi/aot) (bi/pom) (bi/uber)
           (bi/jar) (bi/sift :include #{(re-pattern exec-jar)})
           (bi/target)))

(bc/deftask uberjar
  [] (comp (bt/deps) (bi/aot) (bi/uber)
           (bi/jar) (bi/sift :include #{(re-pattern exec-jar)})
           (bi/target)))

(let [boot? true]
  (if-not boot?
    (when-let [main (resolve (quote boot.user/-main))]
      (main))
    (bc/boot "boot.task.built-in/help")))
