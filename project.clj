(defproject geo-clj "0.5.0-SNAPSHOT"
  :description "Geographic encoding/decoding for Clojure and ClojureScript."
  :url "http://github.com/r0man/geo-clj"
  :author "r0man"
  :min-lein-version "2.6.1"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.5.0"]
                 [com.cognitect/transit-clj "0.8.285" :scope "provided"]
                 [com.cognitect/transit-cljs "0.8.237" :scope "provided"]
                 [noencore "0.2.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228" :scope "provided"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "9.4.1208"]
                 [net.postgis/postgis-jdbc "2.2.0" :exclusions [postgresql]]]
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
                         :optimizations :none
                         :pretty-print true
                         :source-map true
                         :verbose true}
                        :source-paths ["src" "test"]}
                       {:id "advanced"
                        :compiler
                        {:asset-path "target/advanced/out"
                         :main geo.test
                         :output-to "target/advanced/geo-clj.js"
                         :optimizations :advanced
                         :pretty-print true
                         :verbose true}
                        :source-paths ["src" "test"]}]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]]
                   :plugins [[jonase/eastwood "0.2.3"]
                             [lein-cljsbuild "1.1.2"]
                             [lein-difftest "2.0.0"]
                             [lein-doo "0.1.6" :exclusions [org.clojure/clojurescript]]]}})
