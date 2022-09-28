(ns dvd-rental-store.db
  (:require [dvd-rental-store.utils :refer [get-config]]
            [next.jdbc :as jdbc]))

(defn get-connection
  "Returns a database connection from a configuration file."
  [config-file]
  (-> (get-config config-file)
      jdbc/get-datasource
      jdbc/get-connection))

(defn select-all [conn]
  (jdbc/execute! conn ["SELECT * FROM log"]))

(defn count [conn]
  (-> (jdbc/execute-one! conn ["SELECT COUNT(*) FROM log"])
      :count))
