(ns dvd-rental-store.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

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
      (jdbc/with-options {:builder-fn rs/as-unqualified-lower-maps})))

(def conn (get-connection db-params))

;;; ------------ ;;;
;;; DB functions ;;;
;;; ------------ ;;;

(defn revenue
  "Returns the total revenue of all stores."
  [conn]
  (jdbc/execute-one! conn ["SELECT COUNT(amount) AS total_revenue FROM payment"]))

(defn revenue-by-month
  "Returns the revenue by month of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT
  EXTRACT(MONTH FROM payment_date)::INTEGER AS month,
  COUNT(amount) AS revenue
FROM payment
GROUP BY month
ORDER BY month"]))

(defn revenue-by-week
  "Returns the revenue by week of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT
  EXTRACT(WEEK FROM payment_date)::INTEGER AS week,
  COUNT(amount) AS revenue
FROM payment
GROUP BY week
ORDER BY week"]))

(defn revenue-by-day
  "Returns the revenue by day of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT 
  EXTRACT(MONTH FROM payment_date)::INTEGER AS month,
  EXTRACT(DAY FROM payment_date)::INTEGER AS day,
  COUNT(amount) AS revenue
FROM payment
GROUP BY month, day
ORDER BY month, day
"]))

(defn revenue-by-store
  "Returns the total revenue by store."
  [conn]
  (jdbc/execute! conn ["
SELECT
  cu.store_id, 
  ci.city,
  co.country,
  COUNT(p.amount) AS revenue
FROM payment AS p
LEFT JOIN customer AS cu ON p.customer_id = cu.customer_id
LEFT JOIN store AS s ON cu.store_id = s.store_id
LEFT JOIN address AS a ON s.address_id = a.address_id
LEFT JOIN city AS ci ON a.city_id = ci.city_id
LEFT JOIN country AS co ON ci.country_id = co.country_id
GROUP BY cu.store_id, ci.city, co.country
"]))
