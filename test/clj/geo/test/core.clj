(ns geo.test.core
  (:use clojure.test
        geo.core)
  (:import [geo.core LineString MultiPoint Point Polygon]))

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
    (is (= [[30 10] [10 30] [40 40]] (coordinates l)))
    (is (= "LINESTRING(30 10,10 30,40 40)" (wkt l)))))

(deftest test-multi-point
  (let [mp (multi-point [10 40] [40 30] [20 20] [30 10])]
    (is (instance? MultiPoint mp))
    (is (= [[10 40] [40 30] [20 20] [30 10]] (coordinates mp)))
    (is (= "MULTIPOINT(10 40,40 30,20 20,30 10)" (wkt mp)))))

(deftest test-point
  (let [p (point 30 10)]
    (is (instance? Point p))
    (is (= [30 10] (coordinates p)))
    (is (= "POINT(30 10)" (wkt p))))
  (let [p (point 30 10 0)]
    (is (instance? Point p))
    (is (= [30 10 0] (coordinates p)))
    (is (= "POINT(30 10 0)" (wkt p)))))

(deftest test-polygon
  (let [geo (polygon [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    (is (instance? Polygon geo))
    (is (= [[[30 10] [10 20] [20 40] [40 40] [30 10]]]
           (coordinates geo)))
    (is (= "POLYGON((30 10,10 20,20 40,40 40,30 10))" (wkt geo))))
  (let [geo (polygon [[35 10] [10 20] [15 40] [45 45] [35 10]]
                     [[20 30] [35 35] [30 20] [20 30]])]
    (is (instance? Polygon geo))
    (is (= [[[35 10] [10 20] [15 40] [45 45] [35 10]]
            [[20 30] [35 35] [30 20] [20 30]]]
           (coordinates geo)))
    (is (= "POLYGON((35 10,10 20,15 40,45 45,35 10),(20 30,35 35,30 20,20 30))" (wkt geo)))))

(deftest test-point-x
  (is (= 1 (point-x (point 1 2)))))

(deftest test-point-y
  (is (= 2 (point-y (point 1 2)))))

(deftest test-point-z
  (is (nil? (point-z (point 1 2))))
  (is (= 3 (point-z (point 1 2 3)))))
