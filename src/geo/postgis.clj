(ns geo.postgis
  (:import [org.postgis LineString MultiLineString MultiPoint MultiPolygon Point])
  (:require [geo.core :refer [print-wkt wkt]]))

(extend-protocol IWellKnownText
  LineString
  (wkt [geo]
    (str geo))
  MultiLineString
  (wkt [geo]
    (str geo))
  MultiPolygon
  (wkt [geo]
    (str geo))
  MultiPoint
  (wkt [geo]
    (str geo))
  Point
  (wkt [geo]
    (str geo)))

;; PRINT-DUP

(defmethod print-dup LineString
  [geo writer]
  (print-wkt geo writer))

(defmethod print-dup MultiLineString
  [geo writer]
  (print-wkt geo writer))

(defmethod print-dup MultiPolygon
  [geo writer]
  (print-wkt geo writer))

(defmethod print-dup MultiPoint
  [geo writer]
  (print-wkt geo writer))

(defmethod print-dup Point
  [geo writer]
  (print-wkt geo writer))

;; PRINT-METHOD

(defmethod print-method LineString
  [geo writer]
  (print-wkt geo writer))

(defmethod print-method MultiLineString
  [geo writer]
  (print-wkt geo writer))

(defmethod print-method MultiPolygon
  [geo writer]
  (print-wkt geo writer))

(defmethod print-method MultiPoint
  [geo writer]
  (print-wkt geo writer))

(defmethod print-method Point
  [geo writer]
  (print-wkt geo writer))
