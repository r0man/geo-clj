(ns geo.test
  (:require [doo.runner :refer-macros [doo-tests]]
            [geo.core-test]
            [geo.transit-test]))

(doo-tests 'geo.core-test
           'geo.transit-test)
