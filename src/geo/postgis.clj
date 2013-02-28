(ns geo.postgis
  (:import [org.postgis LineString MultiLineString MultiPoint MultiPolygon Point])
  (:require [geo.core :as core]))

(extend-protocol core/ICoordinate
  LineString
  (coordinates [geo]
    (map core/coordinates (.getPoints geo)))
  Point
  (coordinates [geo]
    (if-let [z (core/point-z geo)]
      [(core/point-x geo) (core/point-y geo) z]
      [(core/point-x geo) (core/point-y geo)]))
  MultiPoint
  (coordinates [geo]
    (map core/coordinates (.getPoints geo))))

(extend-type Point
  core/IPoint
  (point-x [geo]
    (.getX geo))
  (point-y [geo]
    (.getY geo))
  (point-z [geo]
    (if (= 3 (.getDimension geo))
      (.getZ geo))))

(extend-protocol core/IWellKnownText
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

(defn point
  "Make a new Point."
  [x y & [z]]
  (if z
    (Point. x y z)
    (Point. x y)))

(defn line-string
  "Make a new LineString."
  [& coordinates]
  (->> (map (partial apply point) coordinates)
       (into-array Point)
       (LineString.)))

(defn multi-point
  "Make a new MultiPoint."
  [& coordinates]
  (MultiPoint. (into-array Point (map #(apply point %1) coordinates))))

;; PRINT-DUP

(defmethod print-dup LineString
  [geo writer]
  (core/print-wkt "line-string" geo writer))

(defmethod print-dup MultiLineString
  [geo writer]
  (core/print-wkt "multi-line-string" geo writer ))

(defmethod print-dup MultiPoint
  [geo writer]
  (core/print-wkt "multi-point" geo writer))

(defmethod print-dup MultiPolygon
  [geo writer]
  (core/print-wkt "multi-polygon" geo writer))

(defmethod print-dup Point
  [geo writer]
  (core/print-wkt "point" geo writer))

;; PRINT-METHOD

(defmethod print-method LineString
  [geo writer]
  (core/print-wkt "line-string" geo writer))

(defmethod print-method MultiLineString
  [geo writer]
  (core/print-wkt "multi-line-string" geo writer))

(defmethod print-method MultiPoint
  [geo writer]
  (core/print-wkt "multi-point" geo writer))

(defmethod print-method MultiPolygon
  [geo writer]
  (core/print-wkt "multi-polygon" geo writer))

(defmethod print-method Point
  [geo writer]
  (core/print-wkt "point" geo writer))

;; READER

(defn read-line-string
  "Read a LineString from `coordinates`."
  [coordinates] (apply line-string coordinates))

(defn read-multi-point
  "Read a MultiPoint from `coordinates`."
  [coordinates] (apply multi-point coordinates))

(defn read-point
  "Read a Point from `coordinates`."
  [coordinates] (apply point coordinates))

(def ^:dynamic *readers*
  {'geo/line-string read-line-string
   'geo/multi-point read-multi-point
   'geo/point read-point})