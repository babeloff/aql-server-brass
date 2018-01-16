
(set-env!
 :source-paths #{"aql/src"}
 :dependencies
 '[[seancorfield/boot-tools-deps "0.1.4"]
   [adzerk/boot-test "1.2.0"]])

(require '[boot-tools-deps.core :refer [deps]])
(require '[adzerk.boot-test :as boot-test])

(task-options!
 pom {:project 'aql-server
      :version "2018.01.15"}
 jar {:manifest {"foo" "bar"}}

 (deftask build
   "Build and install the project locally."
   []
   (comp (deps) (pom) (jar) ))

 (deftask test
   "Runs tests"
   []
   (comp (deps :aliases [:test])
         (boot-test/test))))

(deftask dev
  "profile for repl development"
  []
  (println "dev profile")
  (set-env!
        :init-ns 'user
        :source-paths #(into % ["dev" "test"])
        :dependencies
               #(into % '[[org.clojure/tools.namespace "0.2.11"]]))
  ;(require '[clojure.tools.namespace.repl :as replr])
  (eval '(apply replr/set-refresh-dirs
           (get-env :directories)))
  identity)
