(ns geo.test.util
  (:use clojure.test
        geo.util))

(deftest test-parse-double
  (testing "junk allowed"
    (are [string]
      (is (nil? (parse-double string :junk-allowed true)))
      nil "" "junk"))
  (testing "junk not allwed"
    (are [junk]
      (is (thrown? Exception (parse-double junk)))
      nil "" "junk"))
  (testing "valid strings"
    (are [string expected]
      (is (= (parse-double string) expected))
      0.1 0.1
      "0.2" 0.2
      "324.12" 324.12)))