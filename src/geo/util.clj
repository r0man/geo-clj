(ns geo.util)

(defn parse-double
  "Parse `string` as a double."
  [string & {:keys [junk-allowed]}]
  (if (number? string)
    (double string)
    (try
      (Double/parseDouble string)
      (catch Exception e
        (when-not junk-allowed (throw e))))))