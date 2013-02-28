(ns geo.test.postgis
  (:import [org.postgis LineString MultiLineString MultiPoint MultiPolygon Point])
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
  (let [l (line-string [30 10] [10 30] [40 40])]
    (is (instance? LineString l))
    (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (coordinates l)))
    (is (= "LINESTRING(30 10,10 30,40 40)" (wkt l)))))

(deftest test-multi-point
  (let [mp (multi-point [10 40] [40 30] [20 20] [30 10])]
    (is (instance? MultiPoint mp))
    (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (coordinates mp)))
    (is (= "MULTIPOINT(10 40,40 30,20 20,30 10)" (wkt mp)))))

(deftest test-point
  (let [p (point 30 10)]
    (is (instance? Point p))
    (is (= [30.0 10.0] (coordinates p)))
    (is (= "POINT(30 10)" (wkt p))))
  (let [p (point 30 10 0)]
    (is (instance? Point p))
    (is (= [30.0 10.0 0.0] (coordinates p)))
    (is (= "POINT(30 10 0)" (wkt p)))))

(deftest test-point-x
  (is (= 1.0 (point-x (point 1 2)))))

(deftest test-point-y
  (is (= 2.0 (point-y (point 1 2)))))

(deftest test-point-z
  (is (nil? (point-z (point 1 2))))
  (is (= 3.0 (point-z (point 1 2 3)))))
