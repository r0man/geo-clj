(ns geo.core
  (:require [clojure.string :refer [join]]))

(defprotocol ICoordinate
  (coordinates [o] "Returns the coordinates of the object `o`."))

(defprotocol IPoint
  (point-x [p] "Returns the x coordinate of the point `p`.")
  (point-y [p] "Returns the y coordinate of the point `p`.")
  (point-z [p] "Returns the z coordinate of the point `p`."))

(defprotocol IWellKnownText
  (wkt [o] "Returns `o` as a WKT formatted string."))

(defn- format-position [p]
  (let [[x y z] p]
    (str x " " y (if z (str " " z)))))

(defrecord LineString [coordinates]
  ICoordinate
  (coordinates [p]
    coordinates)
  IWellKnownText
  (wkt [p]
    (str "LINESTRING(" (join ", " (map format-position coordinates)) ")")))

(defrecord MultiLineString [coordinates]
  ICoordinate
  (coordinates [p]
    coordinates))

(defrecord MultiPolygon [coordinates]
  ICoordinate
  (coordinates [p]
    coordinates))

(defrecord MultiPoint [coordinates]
  ICoordinate
  (coordinates [p]
    coordinates)
  IWellKnownText
  (wkt [p]
    (str "MULTIPOINT(" (join ", " (map format-position coordinates)) ")")))

(defrecord Point [coordinates]
  ICoordinate
  (coordinates [p]
    coordinates)
  IPoint
  (point-x [p]
    (nth coordinates 0))
  (point-y [p]
    (nth coordinates 1))
  (point-z [p]
    (nth coordinates 2))
  IWellKnownText
  (wkt [p]
    (str "POINT" (seq coordinates))))

(defrecord Polygon [coordinates]
  ICoordinate
  (coordinates [p]
    coordinates))

(defn point [x y & [z]]
  (->Point (if z [x y z] [x y])))

(defn line-string [& coordinates]
  (->LineString coordinates))

(defn multi-point [& coordinates]
  (->MultiPoint coordinates))

(defn print-wkt [p w]
  (.write w "#wkt \"")
  (.write w (wkt p))
  (.write w "\""))

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
