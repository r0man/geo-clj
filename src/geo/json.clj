(ns geo.json
  (:require [cheshire.core :refer [generate-string parse-string]]
            [cheshire.generate :refer [JSONable encode-map]]
            [clojure.data.json :refer [JSONWriter json-str -write write]]
            [geo.core :refer [coordinates srid]])
  (:import (com.fasterxml.jackson.core JsonGenerator)
           (java.io PrintWriter)))

(defn encode-line-string
  "Encode `line-string` into a GeoJSON compatible data structure."
  [line-string]
  {:type "LineString" :coordinates (coordinates line-string)})

(defn encode-point
  "Encode `point` into a GeoJSON compatible data structure."
  [point]
  {:type "Point" :coordinates (coordinates point)})

(extend-type org.postgis.Point
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-point geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer]
    (write (encode-point geom) writer)))

(extend-type org.postgis.LineString
  JSONable
  (to-json [geom ^JsonGenerator generator]
    (encode-map (encode-line-string geom) generator))
  JSONWriter
  (-write [geom ^PrintWriter writer]
    (write (encode-line-string geom) writer)))
