(ns geo.json-test
  (:require [cheshire.core :refer [generate-string]]
            [clojure.data.json :refer [json-str]]
            [clojure.test :refer :all]
            [geo.json :refer :all]
            [geo.postgis :refer :all]))

(deftest test-encode-line-string
  (let [geom (line-string 4326 [30 10] [10 30] [40 40])]
    (is (= "{\"type\":\"LineString\",\"coordinates\":[[30.0,10.0],[10.0,30.0],[40.0,40.0]]}"
           (generate-string geom)
           (json-str geom)))))

(deftest test-encode-multi-line-string
  (let [geom (multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
    (is (= (str "{\"type\":\"MultiLineString\",\"coordinates\":[[[10.0,10.0],[20.0,20.0],[10.0,40.0]],"
                "[[40.0,40.0],[30.0,30.0],[40.0,20.0],[30.0,10.0]]]}")
           (generate-string geom)
           (json-str geom)))))

(deftest test-encode-point
  (let [geom (point 4326 30 10 0)]
    (is (= "{\"type\":\"Point\",\"coordinates\":[30.0,10.0,0.0]}"
           (generate-string geom)
           (json-str geom)))))
