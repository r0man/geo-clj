(ns geo.transit-test
  #+cljs (:require-macros [cemerick.cljs.test :refer [deftest is are testing]])
  (:require [geo.core :as geo]
            [geo.transit :refer [read-str write-str]]
            #+clj [geo.postgis :as impl]
            #+clj [clojure.test :refer :all]
            #+cljs [geo.core :as impl]
            #+cljs [cemerick.cljs.test :as t]))

(deftest test-encode-line-string
  (let [geom (impl/line-string 4326 [30 10] [10 30] [40 40])]
    (is (= #+clj "[\"~#geo/line-string\",[4326,[[30.0,10.0],[10.0,30.0],[40.0,40.0]]]]"
           #+cljs "[\"~#geo/line-string\",[4326,[[30,10],[10,30],[40,40]]]]"
           (write-str geom)))))

(deftest test-encode-multi-line-string
  (let [geom (impl/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])]
    (is (= #+clj (str "[\"~#geo/multi-line-string\",[4326,[[[10.0,10.0],[20.0,20.0],[10.0,40.0]],[[40.0,40.0],"
                      "[30.0,30.0],[40.0,20.0],[30.0,10.0]]]]]")
           #+cljs (str "[\"~#geo/multi-line-string\",[4326,[[[10,10],[20,20],[10,40]],[[40,40],"
                       "[30,30],[40,20],[30,10]]]]]")
           (write-str geom)))))

(deftest test-encode-multi-polygon
  (let [geom (impl/multi-polygon
              4326
              [[[40 40] [20 45] [45 30] [40 40]]]
              [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])]
    (is (= #+clj (str "[\"~#geo/multi-polygon\",[4326,[[[[40.0,40.0],[20.0,45.0],[45.0,30.0],[40.0,40.0]]],"
                      "[[[20.0,35.0],[45.0,20.0],[30.0,5.0],[10.0,10.0],[10.0,30.0],[20.0,35.0]],[[30.0,20.0],"
                      "[20.0,25.0],[20.0,15.0],[30.0,20.0]]]]]]")
           #+cljs (str "[\"~#geo/multi-polygon\",[4326,[[[[40,40],[20,45],[45,30],[40,40]]],"
                       "[[[20,35],[45,20],[30,5],[10,10],[10,30],[20,35]],[[30,20],"
                       "[20,25],[20,15],[30,20]]]]]]")
           (write-str geom)))))

(deftest test-encode-multi-point
  (let [geom (impl/multi-point 4326 [10 40] [40 30] [20 20] [30 10])]
    (is (= #+clj (str "[\"~#geo/multi-point\",[4326,[[10.0,40.0],[40.0,30.0],[20.0,20.0],[30.0,10.0]]]]")
           #+cljs (str "[\"~#geo/multi-point\",[4326,[[10,40],[40,30],[20,20],[30,10]]]]")
           (write-str geom)))))

(deftest test-encode-polygon
  (let [geom (impl/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])]
    (is (= #+clj "[\"~#geo/polygon\",[4326,[[[30.0,10.0],[10.0,20.0],[20.0,40.0],[40.0,40.0],[30.0,10.0]]]]]"
           #+cljs "[\"~#geo/polygon\",[4326,[[[30,10],[10,20],[20,40],[40,40],[30,10]]]]]"
           (write-str geom)))))

(deftest test-encode-point
  (let [geom (impl/point 4326 30 10 0)]
    (is (= #+clj "[\"~#geo/point\",[4326,[30.0,10.0,0.0]]]"
           #+cljs "[\"~#geo/point\",[4326,[30,10,0]]]"
           (write-str geom)))))

;; (deftest test-encode-pg-geometry
;;   (let [geom (org.postgis.PGgeometry. (impl/point 4326 30 10 0))]
;;     (is (= "{\"type\":\"Point\",\"coordinates\":[30.0,10.0,0.0]}"
;;            (write-str geom)))))

(deftest test-roundtrip
  (are [x]
    (= x (read-str (write-str x)))
    (impl/line-string 4326 [30 10] [10 30] [40 40])
    (impl/multi-line-string 4326 [[10 10] [20 20] [10 40]] [[40 40] [30 30] [40 20] [30 10]])
    (impl/multi-polygon
     4326
     [[[40 40] [20 45] [45 30] [40 40]]]
     [[[20 35] [45 20] [30 5] [10 10] [10 30] [20 35]] [[30 20] [20 25] [20 15] [30 20]]])
    (impl/multi-point 4326 [10 40] [40 30] [20 20] [30 10])
    (impl/point 4326 30 10 0)
    (impl/polygon 4326 [[30 10] [10 20] [20 40] [40 40] [30 10]])))
