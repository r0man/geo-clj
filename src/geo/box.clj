(ns geo.box
  (:use [clojure.string :only (split trim)]
        geo.location
        geo.util))

(defprotocol IBox
  (north-east [box]
    "Returns the Location north-east of `box`.")
  (south-west [box]
    "Returns the Location south-west of `box`.")
  (to-box [obj]
    "Convert `obj` into a bounding box."))

(defrecord Box [south-west north-east]
  IBox
  (north-east [box]
    (:north-east box))
  (south-west [box]
    (:south-west box))
  (to-box [box]
    box))

(defn box?
  "Returns true if `arg` is a box. A box is anything that returns the
  north-east and south-west as a Location."
  [arg]
  (and (location? (north-east arg))
       (location? (south-west arg))))

(defn make-box
  "Make a new Box."
  ([south-west north-east]
     (Box. south-west north-east))
  ([sw-lat sw-lon ne-lat ne-lon]
     (Box.
      (make-location sw-lat sw-lon)
      (make-location ne-lat ne-lon))))

(defn parse-box
  "Parse `string` as a Box."
  [string & {:keys [junk-allowed]}]
  (try
    (apply make-box (split (trim (str string)) #"(\s*,\s*)|(\s+)"))
    (catch Exception e
      (when-not junk-allowed
        (throw e)))))

(defn north-west
  "Returns the north west location of `box`."
  [box]
  (if-let [box (to-box box)]
    (make-location
     (latitude (north-east box))
     (longitude (south-west box)))))

(defn south-east
  "Returns the south east location of `box`."
  [box]
  (if-let [box (to-box box)]
    (make-location
     (latitude (south-west box))
     (longitude (north-east box)))))

(defn difference-of-latitudes
  "Returns the difference of the latitudes."
  [box]
  (- (latitude (north-east box))
     (latitude (south-west box))))

(defn difference-of-longitudes
  "Returns the difference of the longitudes."
  [box]
  (- (longitude (north-east box))
     (longitude (south-west box))))

(defn center-of-latitudes
  "Returns the center of the latitudes."
  [box]
  (+ (latitude (south-west box))
     (/ (difference-of-latitudes box) 2)))

(defn center-of-longitudes
  "Returns the center of the longitudes."
  [box]
  (+ (longitude (south-west box))
     (/ (difference-of-longitudes box) 2)))

(defn center
  "Returns the center of the boundning box."
  [box]
  (make-location
   (center-of-latitudes box)
   (center-of-longitudes box)))

(defn expand-box
  "Expand the bounding box by width and height."
  [box width height]
  (let [box-box (to-box box)]
    (let [width (/ width 2.0) height (/ height 2.0)]
      (make-box
       (make-location
        (- (latitude (south-west box)) height)
        (- (longitude (south-west box)) width))
       (make-location
        (+ (latitude (north-east box)) height)
        (+ (longitude (north-east box)) width))))))

(defn expand-location
  "Expand the location into a bounding box by width and height."
  [location width height]
  (expand-box (make-box location location) width height))

(defn expand
  "Expand object by width and height."
  [object width height]
  (cond
   (location? object)
   (expand-location object width height)
   (box? object)
   (expand-box object width height)))

(defn crosses-dateline?
  "Returns true if the bounding box crosses the international
  dateline, otherwise false."
  [box]
  (< (longitude (north-east box))
     (longitude (south-west box))))

(defn safe-boxes
  "Returns a sequence of safe bounding boxes which are not crossing
  the international dateline."
  [box]
  (if (crosses-dateline? box)
    [(make-box
      (latitude (south-west box))
      (longitude (south-west box))
      (latitude (north-east box))
      180.0)
     (make-box
      (latitude (south-west box))
      -180.0
      (latitude (north-east box))
      (longitude (north-east box)))]
    [box]))

(extend-type nil
  IBox
  (north-east [_]
    nil)
  (south-west [_]
    nil)
  (to-box [_]
    nil))

(extend-type clojure.lang.IPersistentMap
  IBox
  (south-west [box]
    (:south-west box))
  (north-east [box]
    (:north-east box))
  (to-box [box]
    (if (box? box)
      (Box. (south-west box) (north-east box)))))

(extend-type String
  IBox
  (south-west [string]
    (south-west (to-box string)))
  (north-east [string]
    (north-east (to-box string)))
  (to-box [string]
    (parse-box string :junk-allowed true)))
