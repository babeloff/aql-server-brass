
(def project 'brass-immortals/aql-server)
(def version "2018.02.10")

(set-env!
  :source-paths #{"src"}
  :dependencies '[[seancorfield/boot-tools-deps "0.4.0"]])

(require '[boot-tools-deps.core :as deps])

(task-options!
 pom {:project     project
      :version     version
      :description "A server for running AQL programs"
      :url         "https://github.com/babeloff/aql-server-brass/wiki"
      :scm         {:url "https://github.com/babeloff/aql-server-brass"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 jar {:main 'aql.brass.server/main}
 install {}
 deps/deps {}
 repl {}
 push {}
 uber {:as-jars true})

(deftask build
  "Build and install the project locally."
  []
  (comp (deps/deps) (pom) (jar) (install)))

(deftask mrepl
  "my repl."
  []
  (comp (deps/deps) (repl)))

(deftask deploy
  "Build and deploy the project."
  []
  (comp (deps/deps) (pom) (jar) (push)))

(deftask uberjar
  (comp
   (uber)))
   ;; (sift)
        ;; :include "jar$")
        ;;  :merge "(.*\.jar$):WEB-INF/lib/$1")
   ;; (target)))
