(ns geo.box
  (:use geo.location))

(defprotocol IBox
  (north-east [box]
    "Returns the north-east location of `box`.")
  (south-west [box]
    "Returns the south-west location of `box`.")
  (to-box [obj]
    "Convert `obj` into a bounding box."))

(defn box?
  "Returns true if `arg` is a box. A box is anything that returns the
  north-east and south-west as a location."
  [arg]
  (and (location? (north-east arg))
       (location? (south-west arg))))

(extend-type nil
  IBox
  (north-east [_]
    nil)
  (south-west [_]
    nil)
  (to-box [_]
    nil))
