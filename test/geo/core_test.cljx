(ns geo.core-test
  #+cljs (:require-macros [cemerick.cljs.test :refer [deftest is are testing]])
  (:require [geo.core :as geo]
            #+clj [clojure.test :refer :all]
            #+cljs [cemerick.cljs.test :as t])
  ;; (:import org.postgis.PGgeometry )
  ;; (:require [geo.postgis :refer [geometry]])
  ;; (:use clojure.test geo.core)
  ;; (:import [geo.core LineString MultiLineString MultiPoint MultiPolygon Point Polygon])
  )

;; (deftest test-data-readers
;;   (binding  [*data-readers* (merge *data-readers* *readers*)]
;;     (are [geo]
;;       (is (= geo (read-string (pr-str geo))))
;;       (line-string 4326 [30 10] [10 30] [40 40])
;;       (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
;;       (multi-point 4326 [10 40] [40 30] [20 20] [30 10])
;;       (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
;;       (geo/point 4326 30 10 0)
;;       (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]]))))

(deftest test-line-string
  (let [geo (geo/line-string 4326 [30.0 10.0] [10.0 30.0] [40.0 40.0])]
    ;; (is (instance? LineString geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (geo/coordinates geo)))
    (is (= #+clj "#geo/line-string[4326 [[30.0 10.0] [10.0 30.0] [40.0 40.0]]]"
           #+cljs "#geo/line-string[4326 [[30 10] [10 30] [40 40]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;LINESTRING(30.0 10.0,10.0 30.0,40.0 40.0)"
           #+cljs "SRID=4326;LINESTRING(30 10,10 30,40 40)"
           (geo/ewkt geo)))))

(deftest test-multi-line-string
  (let [geo (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
    ;; (is (instance? MultiLineString geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [[[10.0 10.0] [20.0 20.0] [10.0 40.0]]
            [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]
           (geo/coordinates geo)))
    (is (= #+clj "#geo/multi-line-string[4326 [[[10.0 10.0] [20.0 20.0] [10.0 40.0]] [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]]"
           #+cljs "#geo/multi-line-string[4326 [[[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;MULTILINESTRING((10.0 10.0,20.0 20.0,10.0 40.0),(40.0 40.0,30.0 30.0,40.0 20.0,30.0 10.0))"
           #+cljs "SRID=4326;MULTILINESTRING((10 10,20 20,10 40),(40 40,30 30,40 20,30 10))"
           (geo/ewkt geo)))))

(deftest test-multi-point
  (let [geo (geo/multi-point 4326 [10 40] [40 30] [20 20] [30 10])]
    ;; (is (instance? MultiPoint geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (geo/coordinates geo)))
    (is (= #+clj "#geo/multi-point[4326 [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]]]"
           #+cljs "#geo/multi-point[4326 [[10 40] [40 30] [20 20] [30 10]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;MULTIPOINT(10.0 40.0,40.0 30.0,20.0 20.0,30.0 10.0)"
           #+cljs "SRID=4326;MULTIPOINT(10 40,40 30,20 20,30 10)"
           (geo/ewkt geo)))))

(deftest test-multi-polygon
  (let [geo (geo/multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])]
    ;; (is (instance? MultiPolygon geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]]
            [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]]
             [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]
           (geo/coordinates geo)))
    (is (= #+clj "#geo/multi-polygon[4326 [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]] [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]] [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]]"
           #+cljs "#geo/multi-polygon[4326 [[[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;MULTIPOLYGON(((40.0 40.0,20.0 45.0,45.0 30.0,40.0 40.0)),((20.0 35.0,45.0 20.0,30.0 5.0,10.0 10.0,10.0 30.0,20.0 35.0),(30.0 20.0,20.0 25.0,20.0 15.0,30.0 20.0)))"
           #+cljs "SRID=4326;MULTIPOLYGON(((40 40,20 45,45 30,40 40)),((20 35,45 20,30 5,10 10,10 30,20 35),(30 20,20 25,20 15,30 20)))"
           (geo/ewkt geo)))))

(deftest test-point
  (let [geo (geo/point 4326 30 10)]
    ;; (is (instance? Point geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [30.0 10.0] (geo/coordinates geo)))
    (is (= #+clj "#geo/point[4326 [30.0 10.0]]"
           #+cljs "#geo/point[4326 [30 10]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;POINT(30.0 10.0)"
           #+cljs "SRID=4326;POINT(30 10)"
           (geo/ewkt geo))))
  (let [geo (geo/point 4326 30 10 0)]
    ;; (is (instance? Point geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [30.0 10.0 0.0] (geo/coordinates geo)))
    (is (= #+clj "#geo/point[4326 [30.0 10.0 0.0]]"
           #+cljs "#geo/point[4326 [30 10 0]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;POINT(30.0 10.0 0.0)"
           #+cljs "SRID=4326;POINT(30 10 0)"
           (geo/ewkt geo)))))

(deftest test-polygon
  (let [geo (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    ;; (is (instance? Polygon geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]
           (geo/coordinates geo)))
    (is (= #+clj "#geo/polygon[4326 [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]]"
           #+cljs "#geo/polygon[4326 [[[30 10] [10 20] [20 40] [40 40] [30 10]]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;POLYGON((30.0 10.0,10.0 20.0,20.0 40.0,40.0 40.0,30.0 10.0))"
           #+cljs "SRID=4326;POLYGON((30 10,10 20,20 40,40 40,30 10))"
           (geo/ewkt geo))))
  (let [geo (geo/polygon 4326 [[35 10] [10 20] [15 40] [45 45] [35 10]]
                         [[20 30] [35 35] [30 20] [20 30]])]
    ;; (is (instance? Polygon geo))
    ;; (is (instance? PGgeometry (geometry geo)))
    (is (= [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]]
            [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]
           (geo/coordinates geo)))
    (is (= #+clj "#geo/polygon[4326 [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]] [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]]"
           #+cljs "#geo/polygon[4326 [[[35 10] [10 20] [15 40] [45 45] [35 10]] [[20 30] [35 35] [30 20] [20 30]]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;POLYGON((35.0 10.0,10.0 20.0,15.0 40.0,45.0 45.0,35.0 10.0),(20.0 30.0,35.0 35.0,30.0 20.0,20.0 30.0))"
           #+cljs "SRID=4326;POLYGON((35 10,10 20,15 40,45 45,35 10),(20 30,35 35,30 20,20 30))"
           (geo/ewkt geo)))))

(deftest test-point-x
  (is (= 1.0 (geo/point-x (geo/point 4326 1 2)))))

(deftest test-point-y
  (is (= 2.0 (geo/point-y (geo/point 4326 1 2)))))

(deftest test-point-z
  (is (nil? (geo/point-z (geo/point 4326 1 2))))
  (is (= 3.0 (geo/point-z (geo/point 4326 1 2 3)))))

;; (deftest test-point?
;;   (is (not (geo/point? nil)))
;;   (is (not (geo/point? "")))
;;   (is (geo/point? (geo/point 4326 1 2))))

(deftest test-latitude?
  (testing "valid latitude coordinates"
    (are [number]
      (is (geo/latitude? number))
      -90 -90.0 0 90 90.0))
  (testing "invalid latitude coordinates"
    (are [number]
      (is (not (geo/latitude? number)))
      nil "" -90.1 91 90.1 91)))

(deftest test-longitude?
  (testing "valid longitude coordinates"
    (are [number]
      (is (geo/longitude? number))
      -180 -180.0 0 180 180.0))
  (testing "invalid longitude coordinates"
    (are [number]
      (is (not (geo/longitude? number)))
      nil "" -180.1 181 180.1 181)))






;; (ns geo.core-test
;;   (:require-macros [cemerick.cljs.test :refer [are is deftest with-test run-tests testing]])
;;   (:require [cemerick.cljs.test :as t]
;;             [cljs.reader :as reader]
;;             [geo.core :refer [coordinates ewkt line-string multi-line-string multi-point
;;                               multi-polygon polygon point point-x point-y point-z point?
;;                               latitude? longitude?
;;                               LineString MultiLineString MultiPoint MultiPolygon Point Polygon]]))

;; (deftest test-data-readers
;;   (doseq [geo [(line-string 4326 [30 10] [10 30] [40 40])
;;                (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
;;                (multi-point 4326 [10 40] [40 30] [20 20] [30 10])
;;                (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
;;                (geo/point 4326 30 10 0)
;;                (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]]
;;     (is (= geo (reader/read-string (pr-str geo))))))

;; (deftest test-line-string
;;   (let [geo (line-string 4326 [30 10] [10 30] [40 40])]
;;     (is (instance? LineString geo))
;;     (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (geo/coordinates geo)))
;;     (is (= "#geo/line-string[4326 [[30 10] [10 30] [40 40]]]" (pr-str geo)))
;;     (is (= "SRID=4326;LINESTRING(30 10,10 30,40 40)" (geo/ewkt geo)))))

;; (deftest test-multi-line-string
;;   (let [geo (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
;;     (is (instance? MultiLineString geo))
;;     (is (= [[[10.0 10.0] [20.0 20.0] [10.0 40.0]]
;;             [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]
;;            (geo/coordinates geo)))
;;     (is (= "#geo/multi-line-string[4326 [[[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]]]]"
;;            (pr-str geo)))
;;     (is (= "SRID=4326;MULTILINESTRING((10 10,20 20,10 40),(40 40,30 30,40 20,30 10))"
;;            (geo/ewkt geo)))))

;; (deftest test-multi-point
;;   (let [geo (multi-point 4326 [10 40] [40 30] [20 20] [30 10])]
;;     (is (instance? MultiPoint geo))
;;     (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (geo/coordinates geo)))
;;     (is (= "#geo/multi-point[4326 [[10 40] [40 30] [20 20] [30 10]]]" (pr-str geo)))
;;     (is (= "SRID=4326;MULTIPOINT(10 40,40 30,20 20,30 10)" (geo/ewkt geo)))))

;; (deftest test-multi-polygon
;;   (let [geo (multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])]
;;     (is (instance? MultiPolygon geo))
;;     (is (= [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]]
;;             [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]]
;;              [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]
;;            (geo/coordinates geo)))
;;     (is (= "#geo/multi-polygon[4326 [[[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]]]]"
;;            (pr-str geo)))
;;     (is (= "SRID=4326;MULTIPOLYGON(((40 40,20 45,45 30,40 40)),((20 35,45 20,30 5,10 10,10 30,20 35),(30 20,20 25,20 15,30 20)))"
;;            (geo/ewkt geo)))))

;; (deftest test-point
;;   (let [geo (geo/point 4326 30 10)]
;;     (is (instance? Point geo))
;;     (is (= [30.0 10.0] (geo/coordinates geo)))
;;     (is (= "#geo/point[4326 [30 10]]" (pr-str geo)))
;;     (is (= "SRID=4326;POINT(30 10)" (geo/ewkt geo))))
;;   (let [geo (geo/point 4326 30 10 0)]
;;     (is (instance? Point geo))
;;     (is (= [30.0 10.0 0.0] (geo/coordinates geo)))
;;     (is (= "#geo/point[4326 [30 10 0]]" (pr-str geo)))
;;     (is (= "SRID=4326;POINT(30 10 0)" (geo/ewkt geo)))))

;; (deftest test-polygon
;;   (let [geo (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]
;;     (is (instance? Polygon geo))
;;     (is (= [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]
;;            (geo/coordinates geo)))
;;     (is (= "#geo/polygon[4326 [[[30 10] [10 20] [20 40] [40 40] [30 10]]]]"
;;            (pr-str geo)))
;;     (is (= "SRID=4326;POLYGON((30 10,10 20,20 40,40 40,30 10))" (geo/ewkt geo))))
;;   (let [geo (geo/polygon 4326 [[35 10] [10 20] [15 40] [45 45] [35 10]]
;;                      [[20 30] [35 35] [30 20] [20 30]])]
;;     (is (instance? Polygon geo))
;;     (is (= [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]]
;;             [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]
;;            (geo/coordinates geo)))
;;     (is (= "#geo/polygon[4326 [[[35 10] [10 20] [15 40] [45 45] [35 10]] [[20 30] [35 35] [30 20] [20 30]]]]"
;;            (pr-str geo)))
;;     (is (= "SRID=4326;POLYGON((35 10,10 20,15 40,45 45,35 10),(20 30,35 35,30 20,20 30))"
;;            (geo/ewkt geo)))))

;; (deftest test-point?
;;   (is (not (geo/point? nil)))
;;   (is (not (geo/point? "")))
;;   (is (geo/point? (geo/point 4326 1 2))))

;; (deftest test-latitude?
;;   (testing "valid latitude coordinates"
;;     (are [number]
;;       (is (latitude? number))
;;       -90 -90.0 0 90 90.0))
;;   (testing "invalid latitude coordinates"
;;     (are [number]
;;       (is (not (latitude? number)))
;;       nil "" -90.1 91 90.1 91)))

;; (deftest test-longitude?
;;   (testing "valid longitude coordinates"
;;     (are [number]
;;       (is (longitude? number))
;;       -180 -180.0 0 180 180.0))
;;   (testing "invalid longitude coordinates"
;;     (are [number]
;;       (is (not (longitude? number)))
;;       nil "" -180.1 181 180.1 181)))
