(defproject geo-clj "0.3.21-SNAPSHOT"
  :description "Geographic encoding/decoding for Clojure and ClojureScript."
  :url "http://github.com/r0man/geo-clj"
  :author "r0man"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.4.0"]
                 [com.cognitect/transit-clj "0.8.271"]
                 [com.cognitect/transit-cljs "0.8.207"]
                 [noencore "0.1.20"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3208" :scope "provided"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [org.postgis/postgis-jdbc "1.3.3" :exclusions [postgresql]]]
  :aliases {"ci" ["do" ["difftest"] ["lint"]]
            "cleantest" ["do" "clean," "cljx" "once," "test," "cljsbuild" "test"]
            "lint" ["do"  ["eastwood"]]
            "test-ancient" ["test"]}
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "target/classes"
                   :rules :clj}
                  {:source-paths ["src"]
                   :output-path "target/classes"
                   :rules :cljs}
                  {:source-paths ["test"]
                   :output-path "target/test-classes"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "target/test-classes"
                   :rules :cljs}]}
  :cljsbuild {:test-commands {"node" ["node" :node-runner "target/testable.js"]
                              "phantom" ["phantomjs" :runner "target/testable.js"]}
              :builds [{:source-paths ["target/classes" "target/test-classes"]
                        :compiler {:output-to "target/testable.js"
                                   :optimizations :advanced
                                   :pretty-print true}}]}
  :deploy-repositories [["releases" :clojars]]
  :prep-tasks [["cljx" "once"]]
  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.0"]]
                   :plugins [[com.cemerick/clojurescript.test "0.3.1"]
                             [com.keminglabs/cljx "0.6.0"]
                             [jonase/eastwood "0.2.1"]
                             [lein-cljsbuild "1.0.5"]
                             [lein-difftest "2.0.0"]]
                   :repl-options {:nrepl-middleware [cljx.repl-middleware/wrap-cljx]}
                   :test-paths ["target/test-classes"]}
             :test {:prep-tasks [["cljsbuild" "once"]]}})
