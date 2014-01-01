(defproject geo-clj "0.3.7"
  :description "Geographic encoding/decoding for Clojure and ClojureScript."
  :url "http://github.com/r0man/geo-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :lein-release {:deploy-via :clojars}
  :dependencies [[noencore "0.1.10"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.postgis/postgis-jdbc "1.3.3"]]
  :profiles {:dev {:dependencies [[com.keminglabs/cljx "0.3.2"]]
                   :plugins [[com.cemerick/austin "0.1.1"]
                             [com.cemerick/clojurescript.test "0.2.1"]]
                   :repl-options {:nrepl-middleware [cljx.repl-middleware/wrap-cljx]}}}
  :plugins [[com.keminglabs/cljx "0.3.2"]
            [lein-cljsbuild "1.0.1"]]
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
  :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
              :builds [{:source-paths ["target/classes" "target/test-classes"]
                        :compiler {:output-to "target/testable.js"
                                   :optimizations :advanced
                                   :pretty-print true}}]}
  :test-paths ["target/test-classes"])
