(ns geo.core
  (:require [clojure.string :refer [join split]]
            [no.en.core :refer [parse-double]]
            #+cljs [cljs.reader :as reader]))

(defprotocol ICoordinate
  (coordinates [obj] "Returns the coordinates of `obj`.")
  (srid [obj] "Returns spatial reference system identifier `obj`."))

(defprotocol IPoint
  (point? [arg] "Returns true if `arg` is a point, otherwise false.")
  (point-x [point] "Returns the x coordinate of `point`.")
  (point-y [point] "Returns the y coordinate of `point`.")
  (point-z [point] "Returns the z coordinate of `point`."))

(defprotocol IWellKnownText
  (ewkt [obj] "Returns `obj` as a WKT formatted string."))

(defn- format-position [p]
  (let [[x y z] p]
    (str x " " y (if z (str " " z)))))

(defn latitude?
  "Returns true if `latitude` is a number and betweeen -90.0 and 90.0,
  otherwise false."
  [latitude]
  (let [number (parse-double latitude)]
    (and (number? number)
         (>= number -90.0)
         (<= number 90.0))))

(defn longitude?
  "Returns true if `longitude` is a number and between -180.0 and
  180.0, otherwise false."
  [longitude]
  (let [number (parse-double longitude)]
    (and (number? number)
         (>= number -180.0)
         (<= number 180.0))))

(extend-protocol IPoint
  nil
  (point? [_] false)
  #+clj Object
  #+cljs default
  (point? [_] false))

(defrecord LineString [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (str "SRID=" srid ";LINESTRING(" (join "," (map format-position coordinates)) ")")))

(defrecord MultiLineString [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (let [coordinates (map #(str "(" (join "," (map format-position %1)) ")") coordinates)]
      (str "SRID=" srid ";MULTILINESTRING(" (join "," coordinates) ")"))))

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
      (str "SRID=" srid ";MULTIPOLYGON(" (join "," coordinates) ")"))))

(defrecord MultiPoint [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (str "SRID=" srid ";MULTIPOINT(" (join "," (map format-position coordinates)) ")")))

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
  (point? [_]
    true)
  IWellKnownText
  (ewkt [geo]
    (str "SRID=" srid ";POINT(" (format-position coordinates) ")")))

(defrecord Polygon [srid coordinates]
  ICoordinate
  (coordinates [geo]
    coordinates)
  (srid [geo]
    srid)
  IWellKnownText
  (ewkt [geo]
    (let [coordinates (map #(str "(" (join "," (map format-position %1)) ")") coordinates)]
      (str "SRID=" srid ";POLYGON(" (join "," coordinates) ")"))))

(defn point
  "Make a new Point."
  [srid x y & [z]]
  (->Point
   srid
   (if z
     [(double x) (double y) (double z)]
     [(double x) (double y)])))

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

#+clj
(defn print-geo
  "Print the geometric `obj` as `type` to `writer`."
  [type obj writer]
  (.write writer (str "#geo/" (name type) "[" (srid obj) " "))
  (.write writer (str (pr-str (coordinates obj)) "]")))

(defn parse-location [s]
  (if (point? s)
    s (let [parts (->> (split s #"\s*,\s*")
                       (map parse-double)
                       (remove nil?))]
        (if (= 2 (count parts))
          (apply point 4326 (reverse parts))))))

;; PRINT-DUP

#+clj
(defmethod print-dup LineString
  [geo writer]
  (print-geo :line-string geo writer))

#+clj
(defmethod print-dup MultiLineString
  [geo writer]
  (print-geo :multi-line-string geo writer ))

#+clj
(defmethod print-dup MultiPoint
  [geo writer]
  (print-geo :multi-point geo writer))

#+clj
(defmethod print-dup MultiPolygon
  [geo writer]
  (print-geo :multi-polygon geo writer))

#+clj
(defmethod print-dup Point
  [geo writer]
  (print-geo :point geo writer))

#+clj
(defmethod print-dup Polygon
  [geo writer]
  (print-geo :polygon geo writer))

;; PRINT-METHOD

#+clj
(defmethod print-method LineString
  [geo writer]
  (print-geo :line-string geo writer))

#+clj
(defmethod print-method MultiLineString
  [geo writer]
  (print-geo :multi-line-string geo writer))

#+clj
(defmethod print-method MultiPoint
  [geo writer]
  (print-geo :multi-point geo writer))

#+clj
(defmethod print-method MultiPolygon
  [geo writer]
  (print-geo :multi-polygon geo writer))

#+clj
(defmethod print-method Point
  [geo writer]
  (print-geo :point geo writer))

#+clj
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

#+cljs
(defn register-tag-parsers! []
  (doseq [[tag f] *readers*]
    (reader/register-tag-parser! tag f)))

#+cljs
(register-tag-parsers!)

#+cljs
(defn print-geo
  "Print the geometric `obj` as `type` to `writer`."
  [type obj writer]
  (-write writer (str "#geo/" (name type) "[" (srid obj) " "))
  (-write writer (str (pr-str (coordinates obj)) "]")))

;; PRINTER

#+cljs
(extend-protocol IPrintWithWriter
  LineString
  (-pr-writer [geo writer opts]
    (print-geo :line-string geo writer))
  MultiLineString
  (-pr-writer [geo writer opts]
    (print-geo :multi-line-string geo writer))
  MultiPoint
  (-pr-writer [geo writer opts]
    (print-geo :multi-point geo writer))
  MultiPolygon
  (-pr-writer [geo writer opts]
    (print-geo :multi-polygon geo writer))
  Point
  (-pr-writer [geo writer opts]
    (print-geo :point geo writer))
  Polygon
  (-pr-writer [geo writer opts]
    (print-geo :polygon geo writer)))
