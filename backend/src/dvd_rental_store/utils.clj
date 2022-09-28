(ns dvd-rental-store.utils)

(defn get-config [config-file]
  (-> config-file
      slurp
      clojure.edn/read-string))
