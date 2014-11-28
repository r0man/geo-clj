(ns geo.postgis-test
  (:import [org.postgis PGgeometry LineString LinearRing MultiLineString])
  (:import [org.postgis MultiPoint MultiPolygon Point Polygon])
  (:require [clojure.test :refer :all]
            [geo.core :refer [coordinates ewkt point-x point-y point-z point?]]
            [geo.postgis :refer :all]))

(deftest test-data-readers
  (binding  [*data-readers* (merge *data-readers* *readers*)]
    (are [geo]
      (is (= geo (read-string (pr-str geo))))
      (line-string 4326 [30 10] [10 30] [40 40])
      (multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
      (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
      (multi-point 4326 [10 40] [40 30] [20 20] [30 10])
      (point 4326 30 10 0)
      (polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]]))))

(deftest test-line-string
  (let [geo (line-string 4326 [30 10] [10 30] [40 40])]
    (is (instance? LineString geo))
    (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (coordinates geo)))
    (is (= "#geo/line-string[4326 [[30.0 10.0] [10.0 30.0] [40.0 40.0]]]" (pr-str geo)))
    (is (= "SRID=4326;LINESTRING(30 10,10 30,40 40)" (ewkt geo)))
    (is (instance? PGgeometry (geometry geo)))))

(deftest test-linear-ring
  (let [geo (linear-ring 4326 [30 10] [10 20] [20 40] [40 40] [30 10])]
    (is (instance? LinearRing geo))
    (is (= [[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]] (coordinates geo)))
    (is (= "SRID=4326;(30 10,10 20,20 40,40 40,30 10)" (ewkt geo)))
    (is (instance? PGgeometry (geometry geo)))))

(deftest test-multi-line-string
  (let [geo (multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
    (is (instance? MultiLineString geo))
    (is (= [[[10.0 10.0] [20.0 20.0] [10.0 40.0]]
            [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]
           (coordinates geo)))
    (is (= "#geo/multi-line-string[4326 [[[10.0 10.0] [20.0 20.0] [10.0 40.0]] [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]]"
           (pr-str geo)))
    (is (= "SRID=4326;MULTILINESTRING((10 10,20 20,10 40),(40 40,30 30,40 20,30 10))"
           (ewkt geo)))
    (is (instance? PGgeometry (geometry geo)))))

(deftest test-multi-polygon
  (let [geo (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])]
    (is (instance? MultiPolygon geo))
    (is (= [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]]
            [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]]
             [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]
           (coordinates geo)))
    (is (= "#geo/multi-polygon[4326 [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]] [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]] [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]]"
           (pr-str geo)))
    (is (= "SRID=4326;MULTIPOLYGON(((40 40,20 45,45 30,40 40)),((20 35,45 20,30 5,10 10,10 30,20 35),(30 20,20 25,20 15,30 20)))"
           (ewkt geo)))
    (is (instance? PGgeometry (geometry geo)))))

(deftest test-multi-point
  (let [geo (multi-point 4326 [10 40] [40 30] [20 20] [30 10])]
    (is (instance? MultiPoint geo))
    (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (coordinates geo)))
    (is (= "#geo/multi-point[4326 [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]]]" (pr-str geo)))
    (is (= "SRID=4326;MULTIPOINT(10 40,40 30,20 20,30 10)" (ewkt geo)))
    (is (instance? PGgeometry (geometry geo)))))

(deftest test-point
  (let [geo (point 4326 30 10)]
    (is (instance? Point geo))
    (is (= [30.0 10.0] (coordinates geo)))
    (is (= "#geo/point[4326 [30.0 10.0]]" (pr-str geo)))
    (is (= "SRID=4326;POINT(30 10)" (ewkt geo)))
    (is (instance? PGgeometry (geometry geo))))
  (let [geo (point 4326 30 10 0)]
    (is (instance? Point geo))
    (is (= [30.0 10.0 0.0] (coordinates geo)))
    (is (= "#geo/point[4326 [30.0 10.0 0.0]]" (pr-str geo)))
    (is (= "SRID=4326;POINT(30 10 0)" (ewkt geo)))
    (is (instance? PGgeometry (geometry geo)))))

(deftest test-polygon
  (let [geo (polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    (is (instance? Polygon geo))
    (is (= [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]
           (coordinates geo)))
    (is (= "#geo/polygon[4326 [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]]"
           (pr-str geo)))
    (is (= "SRID=4326;POLYGON((30 10,10 20,20 40,40 40,30 10))" (ewkt geo))))
  (let [geo (polygon 4326 [[35 10] [10 20] [15 40] [45 45] [35 10]]
                     [[20 30] [35 35] [30 20] [20 30]])]
    (is (instance? Polygon geo))
    (is (= [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]]
            [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]
           (coordinates geo)))
    (is (= "#geo/polygon[4326 [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]] [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]]"
           (pr-str geo)))
    (is (= "SRID=4326;POLYGON((35 10,10 20,15 40,45 45,35 10),(20 30,35 35,30 20,20 30))" (ewkt geo)))))

(deftest test-point-x
  (is (= 1.0 (point-x (point 4326 1 2)))))

(deftest test-point-y
  (is (= 2.0 (point-y (point 4326 1 2)))))

(deftest test-point-z
  (is (nil? (point-z (point 4326 1 2))))
  (is (= 3.0 (point-z (point 4326 1 2 3)))))

(deftest test-point?
  (is (not (point? nil)))
  (is (point? (point 4326 1 2))))

(deftest test-parse-location
  (is (nil? (parse-location "")))
  (is (nil? (parse-location "x")))
  (is (nil? (parse-location "x,y")))
  (is (= (point 4326 2 1) (parse-location "1,2")))
  (is (= (point 4326 2 1) (parse-location (point 4326 2 1)))))
