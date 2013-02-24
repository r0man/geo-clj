(ns geo.test.core
  (:use clojure.test
        geo.core)
  (:import [geo.core LineString MultiPoint Point]))

(deftest test-line-string
  (let [l (line-string [30 10] [10 30] [40 40])]
    (is (instance? LineString l))
    (is (= [[30 10] [10 30] [40 40]] (:coordinates l)))
    (is (= "LINESTRING(30 10, 10 30, 40 40)" (wkt l)))))

(deftest test-multi-point
  (let [mp (multi-point [10 40] [40 30] [20 20] [30 10])]
    (is (instance? MultiPoint mp))
    (is (= [[10 40] [40 30] [20 20] [30 10]] (:coordinates mp)))
    (is (= "MULTIPOINT(10 40, 40 30, 20 20, 30 10)" (wkt mp)))))

(deftest test-point
  (let [p (point 30 10)]
    (is (instance? Point p))
    (is (= [30 10] (:coordinates p)))
    (is (= "POINT(30 10)" (wkt p))))
  (let [p (point 30 10 0)]
    (is (instance? Point p))
    (is (= [30 10 0] (:coordinates p)))
    (is (= "POINT(30 10 0)" (wkt p)))))