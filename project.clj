(defproject geo-clj "0.1.0"
  :description "Geographic encoding/decoding for Clojure and ClojureScript)."
  :url "http://github.com/r0man/geo-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.postgis/postgis-jdbc "1.3.3"]]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :cljsbuild {:builds [{:compiler {:output-to "target/geo-debug.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :source-paths ["src/cljs"]}
                       {:compiler {:output-to "target/geo-test.js"
                                   :optimizations :advanced
                                   :pretty-print true}
                        :source-paths ["test/cljs"]}
                       {:compiler {:output-to "target/geo.js"
                                   :optimizations :advanced
                                   :pretty-print true}
                        :source-paths ["src/cljs"]
                        :jar true}]
              :repl-listen-port 9000})
