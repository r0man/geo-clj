(ns geo.test
  (:require [geo.core :as geo]))

(defn test-point-x []
  (assert (= 1 (geo/point-x (geo/point 1 2)))))

(defn test-point-y []
  (assert (= 2 (geo/point-y (geo/point 1 2)))))

(defn test-point-z []
  (assert (nil? (geo/point-z (geo/point 1 2))))
  (assert (= 3 (geo/point-z (geo/point 1 2 3)))))

(defn test []
  (test-point-x)
  (test-point-y)
  (test-point-z))

(test)
