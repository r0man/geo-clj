(ns geo.core-test
  #+cljs (:require-macros [cemerick.cljs.test :refer [deftest is are testing]])
  (:require [geo.core :as geo]
            #+clj [clojure.test :refer :all]
            #+cljs [cemerick.cljs.test :as t]
            #+cljs [cljs.reader :as reader]))

(deftest test-data-readers
  #+clj
  (binding  [*data-readers* (merge *data-readers* geo/*readers*)]
    (are [geo]
      (is (= geo (read-string (pr-str geo))))
      (geo/line-string 4326 [30 10] [10 30] [40 40])
      (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
      (geo/multi-point 4326 [10 40] [40 30] [20 20] [30 10])
      (geo/multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
      (geo/point 4326 30 10 0)
      (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])))
  #+cljs
  (doseq [geo [(geo/line-string 4326 [30 10] [10 30] [40 40])
               (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
               (geo/multi-point 4326 [10 40] [40 30] [20 20] [30 10])
               (geo/multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
               (geo/point 4326 30 10 0)
               (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]]
    (is (= geo (reader/read-string (pr-str geo))))))

(deftest test-line-string
  (let [geo (geo/line-string 4326 [30.0 10.0] [10.0 30.0] [40.0 40.0])]
    (is (= [[30.0 10.0] [10.0 30.0] [40.0 40.0]] (geo/coordinates geo)))
    (is (= #+clj "#geo/line-string[4326 [[30.0 10.0] [10.0 30.0] [40.0 40.0]]]"
           #+cljs "#geo/line-string[4326 [[30 10] [10 30] [40 40]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;LINESTRING(30.0 10.0,10.0 30.0,40.0 40.0)"
           #+cljs "SRID=4326;LINESTRING(30 10,10 30,40 40)"
           (geo/ewkt geo)))))

(deftest test-multi-line-string
  (let [geo (geo/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
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
    (is (= [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]] (geo/coordinates geo)))
    (is (= #+clj "#geo/multi-point[4326 [[10.0 40.0] [40.0 30.0] [20.0 20.0] [30.0 10.0]]]"
           #+cljs "#geo/multi-point[4326 [[10 40] [40 30] [20 20] [30 10]]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;MULTIPOINT(10.0 40.0,40.0 30.0,20.0 20.0,30.0 10.0)"
           #+cljs "SRID=4326;MULTIPOINT(10 40,40 30,20 20,30 10)"
           (geo/ewkt geo)))))

(deftest test-multi-polygon
  (let [geo (geo/multi-polygon 4326 [[[40 40] [20 45] [45 30] [40 40]]] [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])]
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
    (is (= [30.0 10.0] (geo/coordinates geo)))
    (is (= #+clj "#geo/point[4326 [30.0 10.0]]"
           #+cljs "#geo/point[4326 [30 10]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;POINT(30.0 10.0)"
           #+cljs "SRID=4326;POINT(30 10)"
           (geo/ewkt geo))))
  (let [geo (geo/point 4326 30 10 0)]
    (is (= [30.0 10.0 0.0] (geo/coordinates geo)))
    (is (= #+clj "#geo/point[4326 [30.0 10.0 0.0]]"
           #+cljs "#geo/point[4326 [30 10 0]]"
           (pr-str geo)))
    (is (= #+clj "SRID=4326;POINT(30.0 10.0 0.0)"
           #+cljs "SRID=4326;POINT(30 10 0)"
           (geo/ewkt geo)))))

(deftest test-polygon
  (let [geo (geo/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]
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
  (is (nil? (geo/point-x nil)))
  (is (= 1.0 (geo/point-x (geo/point 4326 1 2)))))

(deftest test-point-y
  (is (nil? (geo/point-y nil)))
  (is (= 2.0 (geo/point-y (geo/point 4326 1 2)))))

(deftest test-point-z
  (is (nil? (geo/point-z nil)))
  (is (nil? (geo/point-z (geo/point 4326 1 2))))
  (is (= 3.0 (geo/point-z (geo/point 4326 1 2 3)))))

(deftest test-point?
  (is (not (geo/point? nil)))
  (is (not (geo/point? "")))
  (is (not (geo/point? "x")))
  (is (geo/point? (geo/point 4326 1 2))))

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

(deftest test-parse-location
  (is (nil? (geo/parse-location "")))
  (is (nil? (geo/parse-location "x")))
  (is (nil? (geo/parse-location "x,y")))
  (is (= (geo/point 4326 2 1) (geo/parse-location "1,2")))
  (is (= (geo/point 4326 2 1) (geo/parse-location (geo/point 4326 2 1)))))

(deftest test-asin
  (are [x expected]
    (is (= expected (geo/asin x)))
    0 0.0
    1 1.5707963267948966))

(deftest test-sin
  (are [x expected]
    (is (= expected (geo/sin x)))
    0 0.0
    1 0.8414709848078965))

(deftest test-cos
  (are [x expected]
    (is (= expected (geo/cos x)))
    0 1.0
    1 0.5403023058681398))

(deftest test-sqrt
  (are [x expected]
    (is (= expected (geo/sqrt x)))
    0 0.0
    1 1.0
    4 2.0))

(deftest test-to-radians
  (are [x expected]
    (is (= expected (geo/to-radians x)))
    0 0.0
    1 0.017453292519943295))

(deftest test-bearing-to
  (are [point-1 point-2 expected]
    (is (= expected (geo/bearing-to point-1 point-2)))
    (geo/point 4326 -0.0983 51.5136) (geo/point 4326 -0.0983 51.5136) 0.0
    (geo/point 4326 -0.0983 51.5136) (geo/point 4326 -0.0015 51.4778) #+clj 120.67420693455387 #+cljs 120.67420693455165))

(deftest test-destination-point
  (are [point bearing distance expected]
    (is (= expected (geo/destination-point point bearing distance)))
    (geo/point 4326 -0.0983 51.5136) 0 0 (geo/point 4326 -0.09829999999998931 51.5136)
    (geo/point 4326 -0.0983 51.5136) 120.67420693455165 7.794 (geo/point 4326 -0.0015046755432425007 51.47780173246189)))

(deftest test-distance-to
  (are [point-1 point-2 expected]
    (let [[x1 y1] point-1
          [x2 y2] point-2]
      (is (= expected (geo/distance-to (geo/point 4326 x1 y1) (geo/point 4326 x2 y2)))))
    [-0.0983 51.5136] [-0.0983 51.5136] 0.0
    [-0.0983 51.5136] [-0.0015 51.4778] 7.794376772579707
    [-0.0015 51.4778] [-0.0983 51.5136] 7.794376772579707))

(deftest test-final-bearing-to
  (are [point-1 point-2 expected]
    (is (= expected (geo/final-bearing-to point-1 point-2)))
    (geo/point 4326 -0.0983 51.5136) (geo/point 4326 -0.0983 51.5136) 180.0
    (geo/point 4326 -0.0983 51.5136) (geo/point 4326 -0.0015 51.4778) #+clj 120.74995889218681 #+cljs 120.74995889218457))
