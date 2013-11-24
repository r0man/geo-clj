(ns geo.core
  (:require [clojure.string :refer [join split]]
            [no.en.core :refer [parse-double]]
            #+cljs [cljs.reader :as reader]
            #+cljs [goog.math :as math]))

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

(def earth-radius 6371.0)

(def pi
  #+clj Math/PI
  #+cljs js/Math.PI)

(defn to-degree
  "Converts an angle measured in degrees to an approximately
  equivalent angle measured in degrees."
  [x]
  #+clj (Math/toDegrees x)
  #+cljs (math/toDegrees x))

(defn to-radian
  "Converts an angle measured in degrees to an approximately
  equivalent angle measured in radians."
  [x]
  #+clj (Math/toRadians x)
  #+cljs (math/toRadians x))

(defn asin
  "Returns the arc sine of `x`."
  [x]
  #+clj (Math/asin x)
  #+cljs (.asin js/Math x))

(defn atan2
  "Returns the arc sine of `x`."
  [y x]
  #+clj (Math/atan2 y x)
  #+cljs (.atan2 js/Math y x))

(defn sin
  "Returns the sine of `x`."
  [x]
  #+clj (Math/sin x)
  #+cljs (.sin js/Math x))

(defn cos
  "Returns the cosine of `x`."
  [x]
  #+clj (Math/cos x)
  #+cljs (.cos js/Math x))

(defn sqrt
  "Returns the square root of `x`."
  [x]
  #+clj (Math/sqrt x)
  #+cljs (.sqrt js/Math x))

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
  #+clj String
  #+cljs string
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

(defn bearing-to
  "Returns the (initial) bearing from `point-1` to the `point-2` in degrees."
  [point-1 point-2]
  (let [lat-1 (to-radian (point-y point-1))
        lat-2 (to-radian (point-y point-2))
        d-lon (to-radian (- (point-x point-2)
                            (point-x point-1)))
        y (* (sin d-lon) (cos lat-2))
        x (- (* (cos lat-1) (sin lat-2))
             (* (sin lat-1) (cos lat-2) (cos d-lon)))]
    (mod (+ (to-degree (atan2 y x))
            360)
         360)))

(defn destination-point
  "Returns the destination point from `point` having travelled the
  given `distance` (in km) on the given initial `bearing` (bearing may
  vary before destination is reached)."
  [point bearing distance]
  (let [distance (/ distance earth-radius)
        bearing (to-radian bearing)
        lat-1 (to-radian (point-y point))
        lon-1 (to-radian (point-x point))
        lat-2 (asin (+ (* (sin lat-1) (cos distance))
                       (* (cos lat-1) (sin distance) (cos bearing))))
        lon-2 (+ lon-1 (atan2 (* (sin bearing) (sin distance) (cos lat-1))
                              (* (- (cos distance)
                                    (* (sin lat-1) (sin lat-2))))))]
    (->Point (srid point)
             [(to-degree (- (mod (+ lon-2 (* 3 pi))
                                 (* 2 pi))
                            pi))
              (to-degree lat-2)])))


(defn distance-to
  ".Returns the distance from `point-1` to `point-2`, in km using the
  Haversine formula."
  [point-1 point-2]
  (let [lon-1 (point-x point-1)
        lon-2 (point-x point-2)
        lat-1 (point-y point-1)
        lat-2 (point-y point-2)
        d-lat (to-radian (- lat-2 lat-1))
        d-lon (to-radian (- lon-2 lon-1))
        lat-1 (to-radian lat-1)
        lat-2 (to-radian lat-2)
        a (+ (* (sin (/ d-lat 2))
                (sin (/ d-lat 2)))
             (* (sin (/ d-lon 2))
                (sin (/ d-lon 2))
                (cos lat-1)
                (cos lat-2)))]
    (* earth-radius 2 (asin (sqrt a)))))

(defn final-bearing-to
  "Returns final bearing arriving at destination `point-2` from
  `point-1`. The final bearing will differ from the initial bearing by
  varying degrees according to distance and latitude."
  [point-1 point-2]
  (let [lat-1 (to-radian (point-y point-2))
        lat-2 (to-radian (point-y point-1))
        d-lon (to-radian (- (point-x point-1)
                            (point-x point-2)))
        y (* (sin d-lon) (cos lat-2))
        x (- (* (cos lat-1) (sin lat-2))
             (* (sin lat-1) (cos lat-2) (cos d-lon)))]
    (mod (+ (to-degree (atan2 y x))
            180)
         360)))
