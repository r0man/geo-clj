(ns geo.postgis
  (:import [org.postgis PGgeometry LineString LinearRing MultiLineString])
  (:import [org.postgis MultiPoint MultiPolygon Point Polygon])
  (:require [geo.core :as core]))

(extend-protocol core/ICoordinate
  LineString
  (coordinates [geo]
    (vec (map core/coordinates (.getPoints geo))))
  (srid [geo]
    (.getSrid geo))
  LinearRing
  (coordinates [geo]
    (vec (map core/coordinates (.getPoints geo))))
  (srid [geo]
    (.getSrid geo))
  Point
  (coordinates [geo]
    (if-let [z (core/point-z geo)]
      [(core/point-x geo) (core/point-y geo) z]
      [(core/point-x geo) (core/point-y geo)]))
  (srid [geo]
    (.getSrid geo))
  MultiLineString
  (coordinates [geo]
    (vec (map core/coordinates (.getLines geo))))
  (srid [geo]
    (.getSrid geo))
  MultiPolygon
  (coordinates [geo]
    (vec (map core/coordinates (.getPolygons geo))))
  (srid [geo]
    (.getSrid geo))
  MultiPoint
  (coordinates [geo]
    (vec (map core/coordinates (.getPoints geo))))
  (srid [geo]
    (.getSrid geo))
  Polygon
  (coordinates [geo]
    (vec (for [n (range 0 (.numRings geo))]
           (core/coordinates (.getRing geo n)))))
  (srid [geo]
    (.getSrid geo)))

(extend-type Point
  core/IPoint
  (point-x [geo]
    (.getX geo))
  (point-y [geo]
    (.getY geo))
  (point-z [geo]
    (if (= 3 (.getDimension geo))
      (.getZ geo)))
  (point? [_]
    true))

(extend-protocol core/IWellKnownText
  LineString
  (ewkt [geo]
    (str geo))
  LinearRing
  (ewkt [geo]
    (str geo))
  MultiLineString
  (ewkt [geo]
    (str geo))
  MultiPolygon
  (ewkt [geo]
    (str geo))
  MultiPoint
  (ewkt [geo]
    (str geo))
  Point
  (ewkt [geo]
    (str geo))
  Polygon
  (ewkt [geo]
    (str geo)))

(defn point
  "Make a new Point."
  [srid x y & [z]]
  (doto (if z (Point. x y z)
            (Point. x y))
    (.setSrid (or srid -1))))

(defn line-string
  "Make a new LineString."
  [srid & coordinates]
  (doto (->> (map (partial apply point srid) coordinates)
             (into-array Point)
             (LineString.))
    (.setSrid (or srid -1))))

(defn linear-ring
  "Make a new LinearRing."
  [srid & coordinates]
  (doto (LinearRing. (into-array Point (map #(apply point srid %1) coordinates)))
    (.setSrid (or srid -1))))

(defn multi-line-string
  "Make a new MultiLineString."
  [srid & coordinates]
  (doto (->> (map (partial apply line-string srid) coordinates)
             (into-array LineString)
             (MultiLineString.))
    (.setSrid (or srid -1))))

(defn multi-point
  "Make a new MultiPoint."
  [srid & coordinates]
  (doto (MultiPoint. (into-array Point (map #(apply point srid %1) coordinates)))
    (.setSrid (or srid -1))))

(defn polygon
  "Make a new Polygon."
  [srid & coordinates]
  (doto (Polygon. (into-array LinearRing (map #(apply linear-ring srid %1) coordinates)))
    (.setSrid (or srid -1))))

(defn multi-polygon
  "Make a new MultiPolygon."
  [srid & coordinates]
  (doto (MultiPolygon. (into-array Polygon (map #(apply polygon srid %1) coordinates)))
    (.setSrid (or srid -1))))

;; PRINT-DUP

(defmethod print-method PGgeometry
  [geo writer]
  (print-method (.getGeometry geo) writer))

(defmethod print-dup LineString
  [geo writer]
  (core/print-geo :line-string geo writer))

(defmethod print-dup MultiLineString
  [geo writer]
  (core/print-geo :multi-line-string geo writer ))

(defmethod print-dup MultiPoint
  [geo writer]
  (core/print-geo :multi-point geo writer))

(defmethod print-dup MultiPolygon
  [geo writer]
  (core/print-geo :multi-polygon geo writer))

(defmethod print-dup Point
  [geo writer]
  (core/print-geo :point geo writer))

(defmethod print-dup Polygon
  [geo writer]
  (core/print-geo :polygon geo writer))

;; PRINT-METHOD

(defmethod print-method LineString
  [geo writer]
  (core/print-geo :line-string geo writer))

(defmethod print-method MultiLineString
  [geo writer]
  (core/print-geo :multi-line-string geo writer))

(defmethod print-method MultiPoint
  [geo writer]
  (core/print-geo :multi-point geo writer))

(defmethod print-method MultiPolygon
  [geo writer]
  (core/print-geo :multi-polygon geo writer))

(defmethod print-method Point
  [geo writer]
  (core/print-geo :point geo writer))

(defmethod print-method Polygon
  [geo writer]
  (core/print-geo :polygon geo writer))

;; READER

(defn read-line-string
  "Read a LineString from `coordinates`."
  [[srid coordinates]] (apply line-string srid coordinates))

(defn read-multi-line-string
  "Read a MultiLineString from `coordinates`."
  [[srid coordinates]] (apply multi-line-string srid coordinates))

(defn read-multi-point
  "Read a MultiPoint from `coordinates`."
  [[srid coordinates]] (apply multi-point srid coordinates))

(defn read-multi-polygon
  "Read a MultiPoint from `coordinates`."
  [[srid coordinates]] (apply multi-polygon srid coordinates))

(defn read-point
  "Read a Point from `coordinates`."
  [[srid coordinates]] (apply point srid coordinates))

(defn read-polygon
  "Read a Polygon from `coordinates`."
  [[srid coordinates]] (apply polygon srid coordinates))

(def ^:dynamic *readers*
  {'geo/line-string read-line-string
   'geo/multi-line-string read-multi-line-string
   'geo/multi-point read-multi-point
   'geo/multi-polygon read-multi-polygon
   'geo/point read-point
   'geo/polygon read-polygon})
