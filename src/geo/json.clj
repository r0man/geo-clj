(ns geo.json
  (:require [cheshire.generate :refer [encode-map JSONable to-json]]
            [clojure.data.json :refer [-write JSONWriter write]]
            [geo.core :refer [coordinates]])
  (:import (com.fasterxml.jackson.core JsonGenerator)
           (java.io PrintWriter)))

(defn encode-line-string
  "Encode `geom` into a GeoJSON compatible line string data
  structure."
  [geom]
  {:type "LineString" :coordinates (coordinates geom)})

(defn encode-multi-line-string
  "Encode `geom` into a GeoJSON compatible multi line string data structure."
  [geom]
  {:type "MultiLineString" :coordinates (coordinates geom)})

(defn encode-multi-polygon
  "Encode `geom` into a GeoJSON compatible multi polygon data structure."
  [geom]
  {:type "MultiPolygon" :coordinates (coordinates geom)})

(defn encode-multi-point
  "Encode `geom` into a GeoJSON compatible multi point data structure."
  [geom]
  {:type "MultiPoint" :coordinates (coordinates geom)})

(defn encode-point
  "Encode `point` into a GeoJSON compatible point data structure."
  [point]
  {:type "Point" :coordinates (coordinates point)})

(defn encode-polygon
  "Encode `geom` into a GeoJSON compatible polygon data structure."
  [geom]
  {:type "Polygon" :coordinates (coordinates geom)})

(extend-type net.postgis.jdbc.geometry.LineString
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-line-string geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (encode-line-string geom) writer options)))

(extend-type net.postgis.jdbc.geometry.MultiLineString
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-multi-line-string geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (encode-multi-line-string geom) writer options)))

(extend-type net.postgis.jdbc.geometry.MultiPolygon
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-multi-polygon geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (encode-multi-polygon geom) writer options)))

(extend-type net.postgis.jdbc.geometry.MultiPoint
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-multi-point geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (encode-multi-point geom) writer options)))

(extend-type net.postgis.jdbc.geometry.Point
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-point geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (encode-point geom) writer options)))

(extend-type net.postgis.jdbc.geometry.Polygon
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-polygon geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (encode-polygon geom) writer options)))

(extend-type net.postgis.jdbc.PGgeometry
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (to-json (.getGeometry geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer options]
    (-write (.getGeometry geom) writer options)))
