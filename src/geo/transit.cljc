(ns geo.transit
  (:require [geo.core :as geo]
            [cognitect.transit :as transit]
            #?(:clj [geo.postgis :as impl]
               :cljs [geo.core :as impl]))
  #?(:clj (:import (java.io ByteArrayInputStream ByteArrayOutputStream))))

(def write-bounding-box
  "Write a bounding box."
  (transit/write-handler
   (constantly "geo/bounding-box")
   (fn [bounding-box]
     [(#?(:clj .getLLB :cljs :south-west) bounding-box)
      (#?(:clj .getURT :cljs :north-east) bounding-box)])
   (constantly nil)))

(def write-line-string
  "Write a line string"
  (transit/write-handler
   (constantly "geo/line-string")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-multi-line-string
  "Write a multi line string"
  (transit/write-handler
   (constantly "geo/multi-line-string")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-multi-point
  "Write a multi point"
  (transit/write-handler
   (constantly "geo/multi-point")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-multi-polygon
  "Write a multi polygon"
  (transit/write-handler
   (constantly "geo/multi-polygon")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-point
  "Write a point"
  (transit/write-handler
   (constantly "geo/point")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def write-polygon
  "Write a polygon"
  (transit/write-handler
   (constantly "geo/polygon")
   (fn [geom] [(geo/srid geom) (geo/coordinates geom)])
   (constantly nil)))

(def read-bounding-box
  "Read a bounding box."
  (transit/read-handler
   (fn [[south-west north-east]]
     (impl/bounding-box south-west north-east))))

(def read-line-string
  "Read a line string"
  (transit/read-handler
   (fn [[srid coordinates]]
     (apply impl/line-string srid coordinates))))

(def read-multi-line-string
  "Read a multi line string"
  (transit/read-handler
   (fn [[srid coordinates]]
     (apply impl/multi-line-string srid coordinates))))

(def read-multi-point
  "Read a multi point"
  (transit/read-handler
   (fn [[srid coordinates]]
     (apply impl/multi-point srid coordinates))))

(def read-multi-polygon
  "Read a multi polygon"
  (transit/read-handler
   (fn [[srid coordinates]]
     (apply impl/multi-polygon srid coordinates))))

(def read-point
  "Read a point"
  (transit/read-handler
   (fn [[srid coordinates]]
     (apply impl/point srid coordinates))))

(def read-polygon
  "Read a polygon"
  (transit/read-handler
   (fn [[srid coordinates]]
     (apply impl/polygon srid coordinates))))

(def read-handlers
  {"geo/bounding-box" read-bounding-box
   "geo/line-string" read-line-string
   "geo/multi-line-string" read-multi-line-string
   "geo/multi-point" read-multi-point
   "geo/multi-polygon" read-multi-polygon
   "geo/point" read-point
   "geo/polygon" read-polygon})

(def write-handlers
  #?(:clj
     {geo.core.LineString write-line-string
      geo.core.MultiLineString write-multi-line-string
      geo.core.MultiPoint write-multi-point
      geo.core.MultiPolygon write-multi-polygon
      geo.core.Point write-point
      geo.core.Polygon write-polygon
      net.postgis.jdbc.PGbox2d write-bounding-box
      net.postgis.jdbc.geometry.LineString write-line-string
      net.postgis.jdbc.geometry.MultiLineString write-multi-line-string
      net.postgis.jdbc.geometry.MultiPoint write-multi-point
      net.postgis.jdbc.geometry.MultiPolygon write-multi-polygon
      net.postgis.jdbc.geometry.Point write-point
      net.postgis.jdbc.geometry.Polygon write-polygon}
     :cljs
     {geo.core.BoundingBox write-bounding-box
      geo.core.LineString write-line-string
      geo.core.MultiLineString write-multi-line-string
      geo.core.MultiPoint write-multi-point
      geo.core.MultiPolygon write-multi-polygon
      geo.core.Point write-point
      geo.core.Polygon write-polygon}))

(defn write-str [x]
  #?(:clj
     (let [output (ByteArrayOutputStream.)
           writer (transit/writer output :json {:handlers write-handlers})
           _ (transit/write writer x)
           ret (.toString output)]
       (.reset output)
       ret)
     :cljs
     (let [writer (transit/writer :json {:handlers write-handlers})]
       (transit/write writer x))))

(defn read-str [s]
  #?(:clj
     (let [input (ByteArrayInputStream. (.getBytes s))
           reader (transit/reader input :json {:handlers read-handlers})]
       (transit/read reader))
     :cljs
     (let [reader (transit/reader :json {:handlers read-handlers})]
       (transit/read reader s))))
