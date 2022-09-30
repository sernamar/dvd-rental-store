(ns dvd-rental-store.db
  (:require [next.jdbc :as jdbc]))

;;; ------------- ;;;
;;; DB connection ;;;
;;; ------------- ;;;

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

;;; ------------ ;;;
;;; DB functions ;;;
;;; ------------ ;;;

(defn total-revenue
  "Returns the total revenue of all stores."
  [conn]
  (jdbc/execute-one! conn ["SELECT COUNT(amount) AS total_revenue FROM payment"]))

(defn total-revenue-by-month
  "Returns the revenue by month of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT
  EXTRACT(MONTH FROM payment_date)::INTEGER AS month,
  COUNT(amount) AS revenue
FROM payment
GROUP BY month
ORDER BY month"]))

(defn total-revenue-by-week
  "Returns the revenue by week of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT
  EXTRACT(WEEK FROM payment_date)::INTEGER AS week,
  COUNT(amount) AS revenue
FROM payment
GROUP BY week
ORDER BY week"]))

(defn total-revenue-by-day
  "Returns the revenue by day of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT 
  EXTRACT(MONTH FROM payment_date)::INTEGER AS MONTH,
  EXTRACT(DAY FROM payment_date)::INTEGER AS DAY,
  COUNT(amount) AS revenue
FROM payment
GROUP BY MONTH, DAY
ORDER BY MONTH, DAY;
"]))
