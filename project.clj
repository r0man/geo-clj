(defproject geo-clj "0.6.5-SNAPSHOT"
  :description "Geographic encoding/decoding for Clojure and ClojureScript."
  :url "http://github.com/r0man/geo-clj"
  :author "r0man"
  :min-lein-version "2.6.1"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.10.0"]
                 [noencore "0.3.6"]
                 [org.clojure/clojure "1.10.2"]
                 [org.clojure/data.json "1.0.0"]]
  :aliases {"ci" ["do"
                  ["test"]
                  ["doo" "phantom" "none" "once"]
                  ["doo" "phantom" "advanced" "once"]
                  ["lint"]]
            "lint" ["do"  ["eastwood"]]}
  :cljsbuild {:builds [{:id "none"
                        :compiler
                        {:asset-path "target/none/out"
                         :main geo.test
                         :output-to "target/none/geo-clj.js"
                         :output-dir "target/none/out"
                         :optimizations :none}
                        :source-paths ["src" "test"]}
                       {:id "advanced"
                        :compiler
                        {:asset-path "target/advanced/out"
                         :main geo.test
                         :output-to "target/advanced/geo-clj.js"
                         :optimizations :advanced}
                        :source-paths ["src" "test"]}]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.3.13"]
                             [lein-cljsbuild "1.1.8"]
                             [lein-difftest "2.0.0"]
                             [lein-doo "0.1.11" :exclusions [org.clojure/clojurescript]]]}
             :provided {:dependencies [[com.cognitect/transit-clj "1.0.324"]
                                       [com.cognitect/transit-cljs "0.8.264"]
                                       [net.postgis/postgis-jdbc "2.5.0" :exclusions [postgresql org.postgresql/postgresql]]
                                       [org.clojure/clojurescript "1.10.597"]
                                       [org.postgresql/postgresql "42.2.18"]]}})
