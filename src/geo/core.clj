(ns geo.core)

(defprotocol IBox
  (-north-east [box]
    "Returns the north-east location of `box`.")
  (-south-west [box]
    "Returns the south-west location of `box`.")
  (to-box [obj]
    "Convert `obj` into a bounding box."))

(defprotocol ILocation
  (-latitude [location]
    "Returns the latitude of `location`.")
  (-longitude [location]
    "Returns the longitude of `location`.")
  (to-location [obj]
    "Convert `obj` into a bounding loction."))

(defn latitude
  "Returns the latitude of `location`."
  [location] (-latitude (to-location location)))

(defn longitude
  "Returns the longitude of `location`."
  [location] (-longitude (to-location location)))

(defn location?
  "Returns true if `arg` is a location. A location is anything that
  returns the latitude and longitude as numbers."
  [arg]
  (and (number? (-latitude arg))
       (number? (-longitude arg))))

(extend-type nil
  IBox
  (-north-east [_]
    nil)
  (-south-west [_]
    nil)
  (to-box [_]
    nil))

(extend-type nil
  ILocation
  (-latitude [_]
    nil)
  (-longitude [_]
    nil)
  (to-location [_]
    nil))
