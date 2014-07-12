(ns geo.json
  (:require [cheshire.core :refer [generate-string parse-string]]
            [cheshire.generate :refer [JSONable encode-map]]
            [clojure.data.json :refer [JSONWriter json-str -write write]]
            [geo.core :refer [coordinates srid]])
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

(defn encode-point
  "Encode `point` into a GeoJSON compatible point data structure."
  [point]
  {:type "Point" :coordinates (coordinates point)})

(extend-type org.postgis.LineString
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-line-string geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer]
    (write (encode-line-string geom) writer)))

(extend-type org.postgis.MultiLineString
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-multi-line-string geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer]
    (write (encode-multi-line-string geom) writer)))

(extend-type org.postgis.MultiPolygon
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-multi-polygon geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer]
    (write (encode-multi-polygon geom) writer)))

(extend-type org.postgis.Point
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-point geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer]
    (write (encode-point geom) writer)))
