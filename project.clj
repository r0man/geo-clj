(defproject geo-clj "0.3.12-SNAPSHOT"
  :description "Geographic encoding/decoding for Clojure and ClojureScript."
  :url "http://github.com/r0man/geo-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :lein-release {:deploy-via :clojars}
  :dependencies [[noencore "0.1.15"]
                 [org.clojure/clojure "1.6.0"]
                 [org.postgis/postgis-jdbc "1.3.3"]]
  :cljsbuild {:builds []}
  :profiles {:dev {:dependencies [[com.keminglabs/cljx "0.3.2"]
                                  [org.clojure/clojurescript "0.0-2202"]]
                   :plugins [[com.keminglabs/cljx "0.3.2"] ;; Must be before Austin: https://github.com/cemerick/austin/issues/37
                             [lein-cljsbuild "1.0.3"]
                             [com.cemerick/austin "0.1.4"]
                             [com.cemerick/clojurescript.test "0.3.0"]]
                   :hooks [cljx.hooks leiningen.cljsbuild]
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
                   :repl-options {:nrepl-middleware [cljx.repl-middleware/wrap-cljx]}
                   :test-paths ["target/test-classes"]}})
