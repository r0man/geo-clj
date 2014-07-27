(ns geo.transit
  (:require [geo.core :as geo]
            [cognitect.transit :as transit]
            #+clj [geo.postgis :as impl]
            #+cljs [geo.core :as impl])
  #+clj (:import (java.io ByteArrayInputStream ByteArrayOutputStream)))

(defn write-handler [tag-fn rep-fn str-rep-fn]
  #+clj (transit/write-handler tag-fn rep-fn str-rep-fn)
  #+cljs (com.cognitect.transit/makeWriteHandler
          #js {:tag tag-fn :rep rep-fn :stringRep str-rep-fn}))

(defn read-handler [from-rep]
  #+clj (transit/read-handler from-rep)
  #+cljs from-rep)

(def write-line-string
  "Write a line string"
  (write-handler
   (constantly "geo/line-string")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-multi-line-string
  "Write a multi line string"
  (write-handler
   (constantly "geo/multi-line-string")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-multi-point
  "Write a multi point"
  (write-handler
   (constantly "geo/multi-point")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-multi-polygon
  "Write a multi polygon"
  (write-handler
   (constantly "geo/multi-polygon")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-point
  "Write a point"
  (write-handler
   (constantly "geo/point")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-polygon
  "Write a polygon"
  (write-handler
   (constantly "geo/polygon")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def read-line-string
  "Read a line string"
  (read-handler
   (fn [[srid coordinates]]
     (apply impl/line-string srid coordinates))))

(def read-multi-line-string
  "Read a multi line string"
  (read-handler
   (fn [[srid coordinates]]
     (apply impl/multi-line-string srid coordinates))))

(def read-multi-point
  "Read a multi point"
  (read-handler
   (fn [[srid coordinates]]
     (apply impl/multi-point srid coordinates))))

(def read-multi-polygon
  "Read a multi polygon"
  (read-handler
   (fn [[srid coordinates]]
     (apply impl/multi-polygon srid coordinates))))

(def read-point
  "Read a point"
  (read-handler
   (fn [[srid coordinates]]
     (apply impl/point srid coordinates))))

(def read-polygon
  "Read a polygon"
  (read-handler
   (fn [[srid coordinates]]
     (apply impl/polygon srid coordinates))))

(def read-handlers
  {"geo/line-string" read-line-string
   "geo/multi-line-string" read-multi-line-string
   "geo/multi-point" read-multi-point
   "geo/multi-polygon" read-multi-polygon
   "geo/point" read-point
   "geo/polygon" read-polygon})

(def write-handlers
  #+clj
  {org.postgis.LineString write-line-string
   org.postgis.MultiLineString write-multi-line-string
   org.postgis.MultiPoint write-multi-point
   org.postgis.MultiPolygon write-multi-polygon
   org.postgis.Point write-point
   org.postgis.Polygon write-polygon}
  #+cljs
  {geo.core.LineString write-line-string
   geo.core.MultiLineString write-multi-line-string
   geo.core.MultiPoint write-multi-point
   geo.core.MultiPolygon write-multi-polygon
   geo.core.Point write-point
   geo.core.Polygon write-polygon})

(defn write-str [x]
  #+clj
  (let [output (ByteArrayOutputStream.)
        writer (transit/writer output :json {:handlers write-handlers})
        _ (transit/write writer x)
        ret (.toString output)]
    (.reset output)
    ret)
  #+cljs
  (let [writer (transit/writer :json {:handlers write-handlers})]
    (transit/write writer x)))

(defn read-str [s]
  #+clj
  (let [input (ByteArrayInputStream. (.getBytes s))
        reader (transit/reader input :json {:handlers read-handlers})]
    (transit/read reader))
  #+cljs
  (let [reader (transit/reader :json {:handlers read-handlers})]
    (transit/read reader s)))
