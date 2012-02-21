(ns geo.location
  (:use [clojure.string :only (join split trim)]
        [geo.util :only (parse-double)]))

(def ^:dynamic *current-location* nil)

(def location-regex #"([+-]?\d+(\.\d+)?),([+-]?\d+(\.\d+)?)")

(defprotocol ILocation
  (latitude [location]
    "Returns the latitude of `location`.")
  (longitude [location]
    "Returns the longitude of `location`.")
  (to-location [obj]
    "Convert `obj` into a bounding loction."))

(defrecord Location [latitude longitude]
  ILocation
  (latitude [location]
    (:latitude location))
  (longitude [location]
    (:longitude location))
  (to-location [location]
    location))

(defn location?
  "Returns true if `arg` is a location. A location is anything that
  returns the latitude and longitude as numbers."
  [arg]
  (and (number? (latitude arg))
       (number? (longitude arg))))

(defn unwrap-longitude [longitude]
  (if (> longitude 180) (- longitude 360) longitude))

(defn make-location [latitude longitude]
  (Location.
   (parse-double latitude)
   (unwrap-longitude (parse-double longitude))))

(defn format-latitude
  "Format the latitude."
  [latitude] (if latitude (format "%.2f" latitude)))

(defn format-longitude
  "Format the longitude."
  [longitude] (format-latitude longitude))

(defn format-location
  "Format the location."
  [location]
  (if (location? location)
    (->> [(format-latitude (latitude location))
          (format-latitude (longitude location))]
         (join ", "))))

(defn parse-location
  "Parse the location."
  [location & {:keys [junk-allowed]}]
  (try
    (let [[latitude longitude] (split (trim location) #"(\s|,)+")]
      (make-location latitude longitude))
    (catch Exception e
      (when-not junk-allowed
        (throw e)))))

(defmacro with-current-location
  "Evaluate `body` with `*current-location*` bound to `location`."
  [location & body]
  `(binding [*current-location* ~location]
     ~@body))

(extend-type nil
  ILocation
  (latitude [_]
    nil)
  (longitude [_]
    nil)
  (to-location [_]
    nil))

(extend-type clojure.lang.IPersistentMap
  ILocation
  (latitude [map]
    (or (:latitude map) (:lat map)))
  (longitude [map]
    (or (:longitude map) (:lng map)))
  (to-location [map]
    (if (location? map)
      (make-location (latitude map) (longitude map)))))

(extend-type String
  ILocation
  (latitude [string]
    (parse-double string :junk-allowed true))
  (longitude [string]
    (parse-double string :junk-allowed true))
  (to-location [string]
    (parse-location string :junk-allowed true)))
