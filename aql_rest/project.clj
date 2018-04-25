(require 'cemerick.pomegranate.aether)
(cemerick.pomegranate.aether/register-wagon-factory!
  "http" #(org.apache.maven.wagon.providers.http.HttpWagon.))

(defproject
 aql-brass-server "2018.94.25-SNAPSHOT"
 :description "A building the aql-brass-server."
 :url "https://github.com/"
 :license {:name "Eclipse Public License"
           :url "http://www.eclipse.org/legal/epl-v10.html"}
 :source-paths ["src" "../aql_lib/src"]
 :dependencies
 [[org.clojure/clojure "1.9.0"]
  [com.cemerick/pomegranate "1.0.0"]
  [org.clojure/tools.deps.alpha "0.5.425"]
  [org.clojure/tools.nrepl  "0.2.12"]
  [http-kit "2.2.0"]
  [javax.xml.bind/jaxb-api "2.3.0"]
  [com.rpl/specter  "1.1.0"]
  [net.catdata/fql  "0.9-SNAPSHOT"
   :classifier "jar-with-deps"]
  [org.clojure/data.json  "0.2.6"]
  [com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider  "2.9.5"]
  [org.clojure/tools.logging  "0.2.3"]
  [ch.qos.logback/logback-classic  "1.0.1"
   :exclusions [org.slf4j/slf4j-nop]]
  [compojure  "1.6.0"]
  [ring/ring-json  "0.4.0"]
  [commons-daemon/commons-daemon  "1.1.0"]
  [javax.servlet/servlet-api  "2.5"]
  [org.clojure/tools.cli  "0.3.5"]
  [net.cgrand/xforms  "0.16.0"]]
 :exclusions [org.slf4j/slf4j-nop]
 :repositories [["central" {:url "https://repo1.maven.org/maven2/"}]
                ["clojars" {:url "https://clojars.org/repo/"}]
                ["nexus" {:url "http://immortals.isis.vanderbilt.edu:8081/repository/maven-releases"
                          :snapshots false}]
                ["nexus-snapshot" {:url "http://immortals.isis.vanderbilt.edu:8081/repository/maven-snapshots"
                                   :snapshots true}]]
 :deploy-repositories
 [["default"
   {:url "http://immortals.isis.vanderbilt.edu:8081/repository/maven-snapshots"
    :snapshots true}]]

 :main aql.brass.daemon
 :aot [aql.brass.daemon])
