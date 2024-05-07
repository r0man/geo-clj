(ns geo.postgis
  (:require [geo.core :as core])
  (:import (net.postgis.jdbc PGbox2d PGgeometry)
           (net.postgis.jdbc.geometry LinearRing LineString MultiLineString
                                      MultiPoint MultiPolygon Point Polygon)))

(defprotocol IGeometry
  (geometry [obj] "Convert `obj` into a PostGIS geometry."))

(defn bounding-box
  "Make a new bounding box."
  [south-west north-east]
  (PGbox2d. south-west north-east))

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

(defn parse-location [s]
  (if-let [p (core/parse-location s)]
    (point (core/srid p)
           (core/point-x p)
           (core/point-y p)
           (core/point-z p))))

(defn print-bounding-box
  "Print the `bounding-box` to `writer`."
  [bounding-box writer]
  (.write writer (str "#geo/bounding-box["))
  (.write writer (str (pr-str (.getLLB bounding-box)) " "))
  (.write writer (str (pr-str (.getURT bounding-box)) "]")))

;; PRINT-DUP

(defmethod print-dup LineString
  [geo writer]
  (core/print-geo :line-string geo writer))

(defmethod print-dup MultiLineString
  [geo writer]
  (core/print-geo :multi-line-string geo writer))

(defmethod print-dup MultiPoint
  [geo writer]
  (core/print-geo :multi-point geo writer))

(defmethod print-dup MultiPolygon
  [geo writer]
  (core/print-geo :multi-polygon geo writer))

(defmethod print-dup PGbox2d
  [bounding-box writer]
  (print-bounding-box bounding-box writer))

(defmethod print-dup PGgeometry
  [geo writer]
  (print-method (.getGeometry geo) writer))

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

(defmethod print-method PGbox2d
  [bounding-box writer]
  (print-bounding-box bounding-box writer))

(defmethod print-method PGgeometry
  [geo writer]
  (print-method (.getGeometry geo) writer))

(defmethod print-method Point
  [geo writer]
  (core/print-geo :point geo writer))

(defmethod print-method Polygon
  [geo writer]
  (core/print-geo :polygon geo writer))

;; READER

(defn read-bounding-box
  "Read a LineString from `coordinates`."
  [[south-west north-east]]
  (PGbox2d. south-west north-east))

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
  {'geo/bounding-box read-bounding-box
   'geo/line-string read-line-string
   'geo/multi-line-string read-multi-line-string
   'geo/multi-point read-multi-point
   'geo/multi-polygon read-multi-polygon
   'geo/point read-point
   'geo/polygon read-polygon})

;; POSTGIS TYPES

(extend-type LineString
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/ICoordinate
  (coordinates [geo]
    (vec (map core/coordinates (.getPoints geo))))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

(extend-type LinearRing
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/ICoordinate
  (coordinates [geo]
    (vec (map core/coordinates (.getPoints geo))))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

(extend-type Point
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/IPoint
  (point-x [geo]
    (.getX geo))
  (point-y [geo]
    (.getY geo))
  (point-z [geo]
    (if (= 3 (.getDimension geo))
      (.getZ geo)))
  (point? [_]
    true)
  core/ICoordinate
  (coordinates [geo]
    (if-let [z (core/point-z geo)]
      [(core/point-x geo) (core/point-y geo) z]
      [(core/point-x geo) (core/point-y geo)]))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

(extend-type MultiLineString
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/ICoordinate
  (coordinates [geo]
    (vec (map core/coordinates (.getLines geo))))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

(extend-type MultiPolygon
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/ICoordinate
  (coordinates [geo]
    (vec (map core/coordinates (.getPolygons geo))))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

(extend-type MultiPoint
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/ICoordinate
  (coordinates [geo]
    (vec (map core/coordinates (.getPoints geo))))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

(extend-type Polygon
  IGeometry
  (geometry [geo]
    (PGgeometry. geo))
  core/ICoordinate
  (coordinates [geo]
    (vec (for [n (range 0 (.numRings geo))]
           (core/coordinates (.getRing geo n)))))
  (srid [geo]
    (.getSrid geo))
  core/IWellKnownText
  (ewkt [geo]
    (str geo)))

;; CORE TYPES

(defn- core-geom [f geo]
  (geometry (apply f (core/srid geo) (core/coordinates geo))))

(extend-type geo.core.BoundingBox
  IGeometry
  (geometry [box]
    (PGbox2d.
     (geometry (:south-west box))
     (geometry (:north-west box)))))

(extend-type geo.core.LineString
  IGeometry
  (geometry [geo]
    (core-geom line-string geo)))

(extend-type geo.core.MultiLineString
  IGeometry
  (geometry [geo]
    (core-geom multi-line-string geo)))

(extend-type geo.core.MultiPolygon
  IGeometry
  (geometry [geo]
    (core-geom multi-polygon geo)))

(extend-type geo.core.MultiPoint
  IGeometry
  (geometry [geo]
    (core-geom multi-point geo)))

(extend-type geo.core.Point
  IGeometry
  (geometry [geo]
    (core-geom point geo)))

(extend-type geo.core.Polygon
  IGeometry
  (geometry [geo]
    (core-geom polygon geo)))
