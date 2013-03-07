(defproject geo-clj "0.2.1-SNAPSHOT"
  :description "Geographic encoding/decoding for Clojure and ClojureScript)."
  :url "http://github.com/r0man/geo-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [org.postgis/postgis-jdbc "1.3.3"]]
  :profiles {:dev {:dependencies [[com.cemerick/clojurescript.test "0.0.1"]]}}
  :plugins [[lein-cljsbuild "0.3.0"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
              [{:compiler {:output-to "target/geo-debug.js"
                           :optimizations :whitespace
                           :pretty-print true}
                :source-paths ["src"]}
               {:compiler {:output-to "target/geo-test.js"
                           :optimizations :whitespace
                           :pretty-print true}
                :source-paths ["test"]}
               {:compiler {:output-to "target/geo.js"
                           :optimizations :advanced
                           :pretty-print false}
                :source-paths ["src"]
                :jar true}]
              :test-commands {"unit-tests" ["runners/phantomjs.js" "target/geo-test.js"]}})
