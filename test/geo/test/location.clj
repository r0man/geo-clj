(ns geo.test.location
  (:use clojure.test
        geo.location))

(deftest test-location-regex
  (are [location]
    (is (re-matches location-regex location))
    "52.50501880109329,13.235617749999967"))

(deftest test-latitude
  (testing "with Location"
    (is (= -35.522452 (latitude (make-location -35.522452 148.045733)))))
  (testing "with Location"
    (is (= -35.522452 (latitude {:latitude -35.522452 :longitude 148.045733})))))

(deftest test-location?
  (is (location? (make-location -35.522452 148.045733)))
  (is (location? {:latitude -35.522452 :longitude 148.045733}))
  (is (location? {:latitude -35.522452 :longitude 148.045733}))
  (is (not (location? {:latitude 1 :longitude nil})))
  (is (not (location? {:latitude nil :longitude 2})))
  (is (not (location? {:latitude nil :longitude nil})))
  (is (not (location? nil)))
  (is (not (location? ""))))

(deftest test-longitude
  (testing "with Location"
    (is (= 148.045733 (longitude (make-location -35.522452 148.045733)))))
  (testing "with map"
    (is (= 148.045733 (longitude {:latitude -35.522452 :longitude 148.045733})))))

(deftest test-format-latitude
  (are [latitude expected]
    (is (= expected (format-latitude latitude)))
    nil nil
    -35.522452 "-35.52"))

(deftest test-format-longitude
  (are [longitude expected]
    (is (= expected (format-longitude longitude)))
    nil nil
    148.045733 "148.05"))

(deftest test-format-location
  (are [location expected]
    (is (= expected (format-location location)))
    nil nil
    (make-location -35.522452 148.045733) "-35.52, 148.05"))

(deftest test-make-location
  (testing "with latitude and longitude"
    (let [location (make-location -35.522452 148.045733)]
      (is (= -35.522452 (:latitude location)))
      (is (= 148.045733 (:longitude location))))))

(deftest test-parse-location
  (testing "junk allowed"
    (are [string]
      (is (nil? (parse-location string :junk-allowed true)))
      nil "" "junk"))
  (testing "junk not allwed"
    (are [junk]
      (is (thrown? Exception (parse-location junk)))
      nil "" "junk"))
  (testing "valid location"
    (are [string expected]
      (is (= (parse-location string) expected))
      "-35.522452,148.045733"
      (make-location -35.522452 148.045733))))

(deftest test-print-dup
  (let [location (make-location -35.522452 148.045733)]
    (is (= location (read-string (prn-str location))))))

(deftest test-to-location
  (are [location expected]
    (is (= expected (to-location location)))
    nil nil
    "" nil
    (make-location -35.522452 148.045733)
    (make-location -35.522452 148.045733)
    "-35.522452 148.045733"
    (make-location -35.522452 148.045733)
    " -35.522452, 148.045733"
    (make-location -35.522452 148.045733)
    " -35.522452,148.045733"
    (make-location -35.522452 148.045733)
    " -35.522452 148.045733 "
    (make-location -35.522452 148.045733)
    {:latitude -35.522452 :longitude 148.045733}
    (make-location -35.522452 148.045733)
    {:lat -35.522452 :lng 148.045733}
    (make-location -35.522452 148.045733)))

(deftest test-with-current-location
  (with-current-location nil
    (is (nil? *current-location*)))
  (with-current-location "-35.522452 148.045733"
    (is (= (make-location -35.522452 148.045733) *current-location*)))
  (with-current-location (make-location -35.522452 148.045733)
    (is (= (make-location -35.522452 148.045733) *current-location*))))
