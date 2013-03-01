(ns geo.core
  (:require [clojure.string :refer [join]]))

(defprotocol ICoordinate
  (coordinates [obj] "Returns the coordinates of `obj`.")
  (srid [obj] "Returns spatial reference system identifier `obj`."))

(defprotocol IPoint
  (point-x [point] "Returns the x coordinate of `point`.")
  (point-y [point] "Returns the y coordinate of `point`.")
  (point-z [point] "Returns the z coordinate of `point`."))

(defprotocol IWellKnownText
  (ewkt [obj] "Returns `obj` as a WKT formatted string."))

(defn- format-position [p]
  (let [[x y z] p]
    (str x " " y (if z (str " " z)))))

(defrecord LineString [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (format "SRID=%s;LINESTRING(%s)" srid (join "," (map format-position coordinates)))))

(defrecord MultiLineString [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (let [coordinates (map #(str "(" (join "," (map format-position %1)) ")") coordinates)]
      (format "SRID=%s;MULTILINESTRING(%s)" srid (join "," coordinates)))))

(defrecord MultiPolygon [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (let [coordinates
          (map (fn [polygon]
                 (str "(" (join "," (map #(str "(" (join "," (map format-position %1)) ")") polygon)) ")"))
               coordinates)]
      (format "SRID=%s;MULTIPOLYGON(%s)" srid (join "," coordinates)))))

(defrecord MultiPoint [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (format "SRID=%s;MULTIPOINT(%s)" srid (join "," (map format-position coordinates)))))

(defrecord Point [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IPoint
  (point-x [geo]
    (nth coordinates 0))
  (point-y [geo]
    (nth coordinates 1))
  (point-z [geo]
    (nth coordinates 2 nil))
  IWellKnownText
  (ewkt [geo]
    (format "SRID=%s;POINT%s" srid (seq coordinates))))

(defrecord Polygon [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (let [coordinates (map #(str "(" (join "," (map format-position %1)) ")") coordinates)]
      (format "SRID=%s;POLYGON(%s)" srid (join "," coordinates)))))

(defn point
  "Make a new Point."
  [srid x y & [z]]
  (->Point
   srid
   (if z
     [(float x) (float y) (float z)]
     [(float x) (float y)])))

(defn line-string
  "Make a new LineString."
  [srid & coordinates]
  (->LineString srid (vec (map #(vec (map float %1)) coordinates))))

(defn multi-point
  "Make a new MultiPoint."
  [srid & coordinates]
  (->MultiPoint srid (vec (map #(vec (map float %1)) coordinates))))

(defn multi-line-string
  "Make a new MultiLineString."
  [srid & coordinates]
  (->MultiLineString
   srid (vec (map (fn [line] (vec (map #(vec (map float %1)) line)))
                  coordinates))))

(defn multi-polygon
  "Make a new MultiPolygon."
  [srid & coordinates]
  (->MultiPolygon
   srid (vec (map (fn [polygon]
                    (vec (map (fn [ring] (vec (map #(vec (map float %1)) ring))) polygon)))
                  coordinates))))

(defn polygon
  "Make a new Polygon."
  [srid & coordinates]
  (->Polygon
   srid (vec (map (fn [ring] (vec (map #(vec (map float %1)) ring)))
                  coordinates))))

(defn print-geo
  "Print the geometric `obj` as `type` to `writer`."
  [type obj writer]
  (.write writer (str "#geo/" (name type) "[" (srid obj) " "))
  (.write writer (str (pr-str (coordinates obj)) "]")))

;; PRINT-DUP

(defmethod print-dup LineString
  [geo writer]
  (print-geo :line-string geo writer))

(defmethod print-dup MultiLineString
  [geo writer]
  (print-geo :multi-line-string geo writer ))

(defmethod print-dup MultiPoint
  [geo writer]
  (print-geo :multi-point geo writer))

(defmethod print-dup MultiPolygon
  [geo writer]
  (print-geo :multi-polygon geo writer))

(defmethod print-dup Point
  [geo writer]
  (print-geo :point geo writer))

(defmethod print-dup Polygon
  [geo writer]
  (print-geo :polygon geo writer))

;; PRINT-METHOD

(defmethod print-method LineString
  [geo writer]
  (print-geo :line-string geo writer))

(defmethod print-method MultiLineString
  [geo writer]
  (print-geo :multi-line-string geo writer))

(defmethod print-method MultiPoint
  [geo writer]
  (print-geo :multi-point geo writer))

(defmethod print-method MultiPolygon
  [geo writer]
  (print-geo :multi-polygon geo writer))

(defmethod print-method Point
  [geo writer]
  (print-geo :point geo writer))

(defmethod print-method Polygon
  [geo writer]
  (print-geo :polygon geo writer))

;; READER

(defn read-line-string
  "Read a LineString from `coordinates`."
  [[srid coordinates]] (->LineString srid coordinates))

(defn read-multi-line-string
  "Read a MultiLineString from `coordinates`."
  [[srid coordinates]] (->MultiLineString srid coordinates))

(defn read-multi-point
  "Read a MultiPoint from `coordinates`."
  [[srid coordinates]] (->MultiPoint srid coordinates))

(defn read-multi-polygon
  "Read a MultiPolygon from `coordinates`."
  [[srid coordinates]] (->MultiPolygon srid coordinates))

(defn read-point
  "Read a Point from `coordinates`."
  [[srid coordinates]] (->Point srid coordinates))

(defn read-polygon
  "Read a Point from `coordinates`."
  [[srid coordinates]] (->Polygon srid coordinates))

(def ^:dynamic *readers*
  {'geo/line-string read-line-string
   'geo/multi-line-string read-multi-line-string
   'geo/multi-point read-multi-point
   'geo/multi-polygon read-multi-polygon
   'geo/point read-point
   'geo/polygon read-polygon})
