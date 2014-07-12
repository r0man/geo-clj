(ns geo.json
  (:require [cheshire.core :refer [generate-string parse-string]]
            [cheshire.generate :refer [JSONable encode-map]]
            [clojure.data.json :refer [JSONWriter json-str -write write]]
            [geo.core :refer [coordinates srid]])
  (:import (com.fasterxml.jackson.core JsonGenerator)
           (java.io PrintWriter)))

(defn encode-point
  "Encode `point` into a GeoJSON compatible data structure."
  [point]
  {:type "Point" :coordinates (coordinates point)})

(extend-type org.postgis.Point
  JSONable
  (to-json [point ^JsonGenerator generator]
    (encode-map (encode-point point) generator))
  JSONWriter
  (-write [point ^PrintWriter writer]
    (write (encode-point point) writer)))
