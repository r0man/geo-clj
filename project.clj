(defproject geo-clj "0.4.2-SNAPSHOT"
  :description "Geographic encoding/decoding for Clojure and ClojureScript."
  :url "http://github.com/r0man/geo-clj"
  :author "r0man"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.5.0"]
                 [com.cognitect/transit-clj "0.8.285" :scope "provided"]
                 [com.cognitect/transit-cljs "0.8.232" :scope "provided"]
                 [noencore "0.2.0"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.189" :scope "provided"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [net.postgis/postgis-jdbc "2.2.0" :exclusions [postgresql]]]
  :aliases {"ci" ["do" ["test"] ["doo" "phantom" "test" "once"] ["lint"]]
            "lint" ["do"  ["eastwood"]]}
  :cljsbuild {:builds [{:id "test"
                        :compiler {:main 'geo.test
                                   :optimizations :advanced
                                   :output-to "target/testable.js"
                                   :pretty-print true}
                        :source-paths ["src" "test"]}]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]]
                   :plugins [[jonase/eastwood "0.2.2"]
                             [lein-cljsbuild "1.1.1"]
                             [lein-difftest "2.0.0"]
                             [lein-doo "0.1.6"]]}})
