(ns geo.test.core
  (:require-macros [cemerick.cljs.test :refer [is deftest with-test run-tests testing]])
  (:require [cemerick.cljs.test :as t]
            [cljs.reader :as reader]
            [geo.core :refer [coordinates ewkt line-string multi-line-string multi-point
                              multi-polygon polygon point point-x point-y point-z
                              LineString MultiLineString MultiPoint MultiPolygon Point Polygon]]))

(deftest test-data-readers
  (doseq [geo [(line-string 4326 [30 10] [10 30] [40 40])
               (multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
               (multi-point 4326 [10 40] [40 30] [20 20] [30 10])
               (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
               (point 4326 30 10 0)
               (polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]]
    (assert (= geo (reader/read-string (pr-str geo))))))

(deftest test-line-string
  (let [geo (line-string 4326 [30 10] [10 30] [40 40])]
    (assert (instance? LineString geo))
    (assert (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (coordinates geo)))
    (assert (= "#geo/line-string[4326 [[30 10] [10 30] [40 40]]]" (pr-str geo)))
    (assert (= "SRID=4326;LINESTRING(30 10,10 30,40 40)" (ewkt geo)))))

(deftest test-multi-line-string
  (let [geo (multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
    (assert (instance? MultiLineString geo))
    (assert (= [[[10.0 10.0] [20.0 20.0] [10.0 40.0]]
                [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]
               (coordinates geo)))
    (assert (= "#geo/multi-line-string[4326 [[[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]]]]"
               (pr-str geo)))
    (assert (= "SRID=4326;MULTILINESTRING((10 10,20 20,10 40),(40 40,30 30,40 20,30 10))"
               (ewkt geo)))))

(deftest test-multi-point
  (let [geo (multi-point 4326 [10 40] [40 30] [20 20] [30 10])]
    (assert (instance? MultiPoint geo))
    (assert (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (coordinates geo)))
    (assert (= "#geo/multi-point[4326 [[10 40] [40 30] [20 20] [30 10]]]" (pr-str geo)))
    (assert (= "SRID=4326;MULTIPOINT(10 40,40 30,20 20,30 10)" (ewkt geo)))))

(deftest test-multi-polygon
  (let [geo (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])]
    (assert (instance? MultiPolygon geo))
    (assert (= [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]]
                [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]]
                 [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]
               (coordinates geo)))
    (assert (= "#geo/multi-polygon[4326 [[[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]]]]"
               (pr-str geo)))
    (assert (= "SRID=4326;MULTIPOLYGON(((40 40,20 45,45 30,40 40)),((20 35,45 20,30 5,10 10,10 30,20 35),(30 20,20 25,20 15,30 20)))"
               (ewkt geo)))))

(deftest test-point
  (let [geo (point 4326 30 10)]
    (assert (instance? Point geo))
    (assert (= [30.0 10.0] (coordinates geo)))
    (assert (= "#geo/point[4326 [30 10]]" (pr-str geo)))
    (assert (= "SRID=4326;POINT(30 10)" (ewkt geo))))
  (let [geo (point 4326 30 10 0)]
    (assert (instance? Point geo))
    (assert (= [30.0 10.0 0.0] (coordinates geo)))
    (assert (= "#geo/point[4326 [30 10 0]]" (pr-str geo)))
    (assert (= "SRID=4326;POINT(30 10 0)" (ewkt geo)))))

(deftest test-polygon
  (let [geo (polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    (assert (instance? Polygon geo))
    (assert (= [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]
               (coordinates geo)))
    (assert (= "#geo/polygon[4326 [[[30 10] [10 20] [20 40] [40 40] [30 10]]]]"
               (pr-str geo)))
    (assert (= "SRID=4326;POLYGON((30 10,10 20,20 40,40 40,30 10))" (ewkt geo))))
  (let [geo (polygon 4326 [[35 10] [10 20] [15 40] [45 45] [35 10]]
                     [[20 30] [35 35] [30 20] [20 30]])]
    (assert (instance? Polygon geo))
    (assert (= [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]]
                [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]
               (coordinates geo)))
    (assert (= "#geo/polygon[4326 [[[35 10] [10 20] [15 40] [45 45] [35 10]] [[20 30] [35 35] [30 20] [20 30]]]]"
               (pr-str geo)))
    (assert (= "SRID=4326;POLYGON((35 10,10 20,15 40,45 45,35 10),(20 30,35 35,30 20,20 30))"
               (ewkt geo)))))

(deftest test-point-x
  (assert (= 1.0 (point-x (point 4326 1 2)))))

(deftest test-point-y
  (assert (= 2.0 (point-y (point 4326 1 2)))))

(deftest test-point-z
  (assert (nil? (point-z (point 4326 1 2))))
  (assert (= 3.0 (point-z (point 4326 1 2 3)))))
