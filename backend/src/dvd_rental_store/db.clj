(ns dvd-rental-store.db
  (:require [next.jdbc :as jdbc]))


;;; DB connection ;;;

(def db-params (-> "config.edn"
                   slurp
                   clojure.edn/read-string
                   :db-params))

(defn get-connection
  "Returns a connection to the database from a map of db parameters (dbtype, dbname, user, etc.)."
  [db-params]
  (-> db-params
      jdbc/get-datasource
      jdbc/get-connection))

(def conn (get-connection db-params))


;;; DB functions ;;;

(defn count-films [conn]
  (jdbc/execute-one! conn ["SELECT COUNT(*) FROM film"]))
