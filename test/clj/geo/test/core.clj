(ns geo.test.core
  (:use clojure.test geo.core)
  (:import [geo.core LineString MultiLineString MultiPoint MultiPolygon Point Polygon]))

(deftest test-data-readers
  (binding  [*data-readers* (merge *data-readers* *readers*)]
    (are [geo]
         (is (= geo (read-string (pr-str geo))))
         (line-string [30 10] [10 30] [40 40])
         (multi-line-string [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
         (multi-point [10 40] [40 30] [20 20] [30 10])
         (multi-polygon [[[40 40] [20 45] [45 30] [40 40]]]
                        [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]]
                         [[30 20] [20 25] [20 15] [30 20]]])
         (point 30 10 0)
         (polygon [[30 10] [10 20] [20 40] [40 40] [30 10]]))))

(deftest test-line-string
  (let [geo (line-string [30 10] [10 30] [40 40])]
    (is (instance? LineString geo))
    (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (coordinates geo)))
    (is (= "LINESTRING(30.0 10.0,10.0 30.0,40.0 40.0)" (wkt geo)))))

(deftest test-multi-line-string
  (let [geo (multi-line-string
             [[10 10] [20 20] [10 40]]
             [[40 40] [30 30] [40 20] [30 10]])]
    (is (instance? MultiLineString geo))
    (is (= [[[10.0 10.0] [20.0 20.0] [10.0 40.0]]
            [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]
           (coordinates geo)))
    (is (= "MULTILINESTRING((10.0 10.0,20.0 20.0,10.0 40.0),(40.0 40.0,30.0 30.0,40.0 20.0,30.0 10.0))"
           (wkt geo)))))

(deftest test-multi-point
  (let [geo (multi-point [10 40] [40 30] [20 20] [30 10])]
    (is (instance? MultiPoint geo))
    (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (coordinates geo)))
    (is (= "MULTIPOINT(10.0 40.0,40.0 30.0,20.0 20.0,30.0 10.0)" (wkt geo)))))

(deftest test-multi-polygon
  (let [geo (multi-polygon
             [[[40 40] [20 45] [45 30] [40 40]]]
             [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]]
              [[30 20] [20 25] [20 15] [30 20]]])]
    (is (instance? MultiPolygon geo))
    (is (= [[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]]
            [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]]
             [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]
           (coordinates geo)))
    (is (= "MULTIPOLYGON(((40.0 40.0,20.0 45.0,45.0 30.0,40.0 40.0)),((20.0 35.0,45.0 20.0,30.0 5.0,10.0 10.0,10.0 30.0,20.0 35.0),(30.0 20.0,20.0 25.0,20.0 15.0,30.0 20.0)))"
           (wkt geo)))))

(deftest test-point
  (let [geo (point 30 10)]
    (is (instance? Point geo))
    (is (= [30.0 10.0] (coordinates geo)))
    (is (= "POINT(30.0 10.0)" (wkt geo))))
  (let [geo (point 30 10 0)]
    (is (instance? Point geo))
    (is (= [30.0 10.0 0.0] (coordinates geo)))
    (is (= "POINT(30.0 10.0 0.0)" (wkt geo)))))

(deftest test-polygon
  (let [geo (polygon [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    (is (instance? Polygon geo))
    (is (= [[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]
           (coordinates geo)))
    (is (= "POLYGON((30.0 10.0,10.0 20.0,20.0 40.0,40.0 40.0,30.0 10.0))" (wkt geo))))
  (let [geo (polygon [[35 10] [10 20] [15 40] [45 45] [35 10]]
                     [[20 30] [35 35] [30 20] [20 30]])]
    (is (instance? Polygon geo))
    (is (= [[[35.0 10.0] [10.0 20.0] [15.0 40.0] [45.0 45.0] [35.0 10.0]]
            [[20.0 30.0] [35.0 35.0] [30.0 20.0] [20.0 30.0]]]
           (coordinates geo)))
    (is (= "POLYGON((35.0 10.0,10.0 20.0,15.0 40.0,45.0 45.0,35.0 10.0),(20.0 30.0,35.0 35.0,30.0 20.0,20.0 30.0))"
           (wkt geo)))))

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
       (line-string [30 10] [10 30] [40 40])
       "#geo/line-string[[30.0 10.0] [10.0 30.0] [40.0 40.0]]"
       (multi-line-string [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
       "#geo/multi-line-string[[[10.0 10.0] [20.0 20.0] [10.0 40.0]] [[40.0 40.0] [30.0 30.0] [40.0 20.0] [30.0 10.0]]]"
       (multi-point [10 40] [40 30] [20 20] [30 10])
       "#geo/multi-point[[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]]"
       (multi-polygon [[[40 40] [20 45] [45 30] [40 40]]]
                      [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]]
                       [[30 20] [20 25] [20 15] [30 20]]])
       "#geo/multi-polygon[[[[40.0 40.0] [20.0 45.0] [45.0 30.0] [40.0 40.0]]] [[[20.0 35.0] [45.0 20.0] [30.0 5.0] [10.0 10.0] [10.0 30.0] [20.0 35.0]] [[30.0 20.0] [20.0 25.0] [20.0 15.0] [30.0 20.0]]]]"
       (point 1 2)
       "#geo/point[1.0 2.0]"
       (polygon [[30 10] [10 20] [20 40] [40 40] [30 10]])
       "#geo/polygon[[[30.0 10.0] [10.0 20.0] [20.0 40.0] [40.0 40.0] [30.0 10.0]]]"
       (point 1 2)
       "#geo/point[1.0 2.0]"))
