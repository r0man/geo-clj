(ns geo.test.box
  (:use clojure.test
        geo.box
        geo.location))

(deftest test-to-box
  (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
    (is (= box (to-box box)))
    (is (= box (to-box "-35.522452 148.045733 -33.256207 153.242267")))
    (is (= box (to-box "-35.522452,148.045733,-33.256207,153.242267")))
    (is (= box (to-box "-35.522452, 148.045733, -33.256207, 153.242267")))
    (is (= box (to-box " -35.522452, 148.045733, -33.256207, 153.242267 ")))
    (is (= box (to-box {:south-west (south-west box) :north-east (north-east box)})))))

(deftest test-box?
  (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
    (is (box? (make-box -35.522452 148.045733 -33.256207 153.242267)))
    (is (box? {:south-west (south-west box) :north-east (north-east box)})))
  (is (not (box? {:south-west 1 :north-east 2})))
  (is (not (box? nil)))
  (is (not (box? ""))))

(deftest test-expand-box
  (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
    (is (= box (expand box 0 0)))
    (is (= (make-box -36.022452 147.545733 -32.756207 153.742267)
           (expand box 1 1)))))

(deftest test-expand-location
  (is (= (make-box -0.5 -0.5 0.5 0.5)
         (expand (make-location 0 0) 1 1))))

(deftest test-expand
  (testing "with bounding box"
    (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
      (is (= (expand-box box 1 1)
             (expand box 1 1)))))
  (testing "location"
    (is (= (expand-location (make-location 0 0) 1 1)
           (expand (make-location 0 0) 1 1)))))

(deftest test-make-box
  (is (= (make-box -35.522452 148.045733 -33.256207 153.242267)
         (make-box (make-location -35.522452 148.045733)
                   (make-location -33.256207 153.242267))))
  (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
    (is (box? box))))

(deftest test-center
  (let [center (center (make-box -35.522452 148.045733 -33.256207 153.242267))]
    (is (= (latitude center) -34.3893295))
    (is (= (longitude center) 150.644))))

(deftest test-difference-of-latitudes
  (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
    (is (= (difference-of-latitudes box) 2.266244999999998))))

(deftest test-difference-of-longitudes
  (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
    (is (= (difference-of-longitudes box) 5.1965339999999856))))

(deftest test-parse-box
  (testing "with invalid bounds and junk allowed"
    (are [string]
      (is (nil? (parse-box string :junk-allowed true)))
      nil "" "junk"))
  (testing "with invalid bounds and no junk allowed"
    (are [junk]
      (is (thrown? Exception (parse-box junk)))
      nil "" "junk"))
  (testing "with valid bounds"
    (are [string expected]
      (is (= expected (parse-box string)))
      "-35.522452,148.045733,-33.256207,153.242267"
      (make-box -35.522452 148.045733 -33.256207 153.242267)
      "-90,-180,90,180"
      (make-box -90 -180 90 180)
      "35.309237,-10.662763,45.329699,2.301104"
      (make-box 35.309237 -10.662763 45.329699 2.301104))))

(deftest test-north-east
  (is (= (make-location -33.256207 153.242267)
         (north-east (make-box -35.522452 148.045733 -33.256207 153.242267)))))

(deftest test-north-west
  (is (= (make-location -33.256207 148.045733)
         (north-west (make-box -35.522452 148.045733 -33.256207 153.242267)))))

(deftest test-south-east
  (is (= (make-location -35.522452 153.242267)
         (south-east (make-box -35.522452 148.045733 -33.256207 153.242267)))))

(deftest test-south-west
  (is (= (make-location -35.522452 148.045733)
         (south-west (make-box -35.522452 148.045733 -33.256207 153.242267)))))

(deftest test-crosses-dateline?
  (is (crosses-dateline? (make-box -44.542775 168.128741 -22.856251 -164.317548)))
  (is (not (crosses-dateline? (make-box -35.522452 148.045733 -33.256207 153.242267)))))

(deftest test-safe-boxes
  (testing "with bounding box not crossing the dateline"
    (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
      (is (= [box] (safe-boxes box)))))
  (testing "with bounding box crossing the dateline"
    (let [box (make-box -44.542775 168.128741 -22.856251 -164.317548)
          safe-boxes (safe-boxes box)]
      (is (= 2 (count box)))
      (let [safe-box (first safe-boxes)]
        (is (= -44.542775 (latitude (south-west safe-box))))
        (is (= 168.128741 (longitude (south-west safe-box))))
        (is (= -22.856251 (latitude (north-east safe-box))))
        (is (= 180.0 (longitude (north-east safe-box)))))
      (let [safe-box (second safe-boxes)]
        (is (= -44.542775 (latitude (south-west safe-box))))
        (is (= -180.0 (longitude (south-west safe-box))))
        (is (= -22.856251 (latitude (north-east safe-box))))
        (is (= -164.317548 (longitude (north-east safe-box))))))))

(deftest test-print-dup
  (binding [*print-dup* true]
    (let [box (make-box -35.522452 148.045733 -33.256207 153.242267)]
      (is (= box (read-string (prn-str box)))))))
