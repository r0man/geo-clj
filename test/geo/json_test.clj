(ns geo.json-test
  (:require [cheshire.core :refer [generate-string]]
            [clojure.data.json :refer [json-str]]
            [clojure.test :refer :all]
            [geo.json :refer :all]))

(deftest test-encode-point
  (is (= "{\"type\":\"Point\",\"coordinates\":[0.0,0.0]}"
         (generate-string (org.postgis.Point. 0 0))
         (json-str (org.postgis.Point. 0 0)))))
