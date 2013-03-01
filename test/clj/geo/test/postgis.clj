(ns geo.test.postgis
  (:import [org.postgis PGgeometry LineString LinearRing MultiLineString])
  (:import [org.postgis MultiPoint MultiPolygon Point Polygon])
  (:require [geo.core :refer [coordinates wkt point-x point-y point-z]])
  (:use clojure.test
        geo.postgis))

(deftest test-data-readers
  (binding  [*data-readers* (merge *data-readers* *readers*)]
    (are [geo]
         (is (= geo (read-string (pr-str geo))))
         (line-string [30 10] [10 30] [40 40])
         (multi-point [10 40] [40 30] [20 20] [30 10])
         (point 30 10 0))))

(deftest test-line-string
  (let [geo (line-string [30 10] [10 30] [40 40])]
    (is (instance? LineString geo))
    (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (coordinates geo)))
    (is (= "LINESTRING(30 10,10 30,40 40)" (wkt geo)))))

(deftest test-linear-ring
  (let [geo (linear-ring [30 10] [10 20] [20 40] [40 40] [30 10])]
    (is (instance? LinearRing geo))
    (is (= [[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]] (coordinates geo)))
    (is (= "(30 10,10 20,20 40,40 40,30 10)" (wkt geo)))))

(deftest test-multi-point
  (let [geo (multi-point [10 40] [40 30] [20 20] [30 10])]
    (is (instance? MultiPoint geo))
    (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (coordinates geo)))
    (is (= "MULTIPOINT(10 40,40 30,20 20,30 10)" (wkt geo)))))

(deftest test-polygon
  (let [geo (polygon [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    (is (instance? Polygon geo))
    (is (= [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]
           (coordinates geo)))
    (is (= "POLYGON((30 10,10 20,20 40,40 40,30 10))" (wkt geo)))))

(deftest test-point
  (let [geo (point 30 10)]
    (is (instance? Point geo))
    (is (= [30.0 10.0] (coordinates geo)))
    (is (= "POINT(30 10)" (wkt geo))))
  (let [geo (point 30 10 0)]
    (is (instance? Point geo))
    (is (= [30.0 10.0 0.0] (coordinates geo)))
    (is (= "POINT(30 10 0)" (wkt geo)))))

(deftest test-point-x
  (is (= 1.0 (point-x (point 1 2)))))

(deftest test-point-y
  (is (= 2.0 (point-y (point 1 2)))))

(deftest test-point-z
  (is (nil? (point-z (point 1 2))))
  (is (= 3.0 (point-z (point 1 2 3)))))

(deftest test-pr-str
  (are [s expected]
       (is (= expected (pr-str s)))
       (point 1 2) "#geo/point[1.0 2.0]"
       (PGgeometry. (point 1 2)) "#geo/point[1.0 2.0]"))