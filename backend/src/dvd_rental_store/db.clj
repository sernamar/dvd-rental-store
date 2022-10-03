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

;;; ----------------- ;;;
;;; Revenue functions ;;;
;;; ----------------- ;;;

(defn revenue
  "Returns the total revenue of all stores."
  [conn]
  (jdbc/execute-one! conn ["
SELECT SUM(amount) AS total_revenue
FROM payment
"]))

(defn revenue-by-month
  "Returns the revenue by month of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT EXTRACT(MONTH
               FROM payment_date)::INTEGER AS month,
       SUM(amount) AS revenue
FROM payment
GROUP BY month
ORDER BY month
"]))

(defn revenue-by-week
  "Returns the revenue by week of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT EXTRACT(WEEK
               FROM payment_date)::INTEGER AS week,
       SUM(amount) AS revenue
FROM payment
GROUP BY week
ORDER BY week;
"]))

(defn revenue-by-day
  "Returns the revenue by day of all stores."
  [conn]
  (jdbc/execute! conn ["
SELECT EXTRACT(MONTH
               FROM payment_date)::INTEGER AS month,
       EXTRACT(DAY
               FROM payment_date)::INTEGER AS day,
       SUM(amount) AS revenue
FROM payment
GROUP BY month,
         day
ORDER BY month,
         day
"]))

(defn revenue-by-store
  "Returns the total revenue by store."
  [conn]
  (jdbc/execute! conn ["
WITH store_details AS
  (SELECT store_id,
          concat(city, ' (', country, ')') AS name
   FROM store
   LEFT JOIN address USING(address_id)
   LEFT JOIN city USING(city_id)
   LEFT JOIN country USING(country_id))
SELECT name AS store,
       SUM(amount) AS revenue
FROM payment
LEFT JOIN customer USING(customer_id)
LEFT JOIN store_details USING(store_id)
GROUP BY store
"]))

;;; ---------------- ;;;
;;; Volume functions ;;;
;;; ---------------- ;;;

(defn volume
  "Returns the total volume of all stores."
  [conn]
  (jdbc/execute-one! conn ["
SELECT COUNT(rental_id) AS total_volume
FROM rental
"]))

(defn volume-by-store
  "Returns the total volume of all stores."
  [conn]
  (jdbc/execute-one! conn ["
WITH store_details AS
  (SELECT store_id,
          concat(city, ' (', country, ')') AS name
   FROM store
   LEFT JOIN address USING(address_id)
   LEFT JOIN city USING(city_id)
   LEFT JOIN country USING(country_id))
SELECT NAME AS store,
       COUNT(rental_id) AS volume
FROM rental
LEFT JOIN customer USING(customer_id)
LEFT JOIN store_details USING(store_id)
GROUP BY store
"]))

(defn volume-by-month-and-store
  "Returns the volume by month and by store."
  [conn]
  (jdbc/execute-one! conn ["
WITH store_details AS
  (SELECT store_id,
          concat(city, ' (', country, ')') AS name
   FROM store
   LEFT JOIN address USING(address_id)
   LEFT JOIN city USING(city_id)
   LEFT JOIN country USING(country_id))
SELECT store_id,
       NAME AS store,
       EXTRACT(MONTH
               FROM rental_date)::INTEGER AS MONTH,
       COUNT(rental_id) AS volume
FROM rental
LEFT JOIN customer USING(customer_id)
LEFT JOIN store_details USING(store_id)
GROUP BY store_id, store, MONTH
ORDER BY store, MONTH
"]))

;;; -------------- ;;;
;;; Film functions ;;;
;;; -------------- ;;;

(defn top-ten-films-by-volume
  "Returns the top-ten most rented films."
  [conn]
  (jdbc/execute! conn ["
SELECT title,
       COUNT(rental_id) AS volume
FROM rental
LEFT JOIN inventory USING(inventory_id)
LEFT JOIN film USING(film_id)
GROUP BY title
ORDER BY volume DESC
LIMIT 10
"]))

(defn top-ten-films-by-revenue
  "Returns the top-ten films that generate more revenue."
  [conn]
  (jdbc/execute! conn ["
SELECT title,
       SUM(rental_id) AS revenue
FROM payment
LEFT JOIN rental USING(rental_id)
LEFT JOIN inventory USING(inventory_id)
LEFT JOIN film USING(film_id)
GROUP BY title
ORDER BY revenue DESC
LIMIT 10
"]))

(defn number-of-films-by-category
  "Returns the number of films in each category."
  [conn]
  (jdbc/execute! conn ["
SELECT name as category,
       COUNT(film_id) AS number_of_films
FROM film_category
LEFT JOIN category USING(category_id)
GROUP BY category
ORDER BY number_of_films DESC
"]))

;;; ------------------ ;;;
;;; Category functions ;;;
;;; ------------------ ;;;

(defn top-categories-by-volume
  "Returns the top categories by volume."
  [conn]
  (jdbc/execute! conn ["
SELECT NAME AS category,
       COUNT(rental_id) AS volume
FROM rental
LEFT JOIN inventory USING(inventory_id)
LEFT JOIN film USING(film_id)
LEFT JOIN film_category USING(film_id)
LEFT JOIN category USING(category_id)
GROUP BY category
ORDER BY volume DESC
"]))

(defn top-categories-by-revenue
  "Returns the top categories by revenue."
  [conn]
  (jdbc/execute! conn ["
SELECT NAME AS category,
       SUM(amount) AS revenue
FROM payment
LEFT JOIN rental USING(rental_id)
LEFT JOIN inventory USING(inventory_id)
LEFT JOIN film USING(film_id)
LEFT JOIN film_category USING(film_id)
LEFT JOIN category USING(category_id)
GROUP BY category
ORDER BY volume DESC
"]))

;;; ------------------ ;;;
;;; Customer functions ;;;
;;; ------------------ ;;;

(defn top-customers-by-volume
  "Returns the top-ten customers that rented most films."
  [conn]
  (jdbc/execute! conn ["
SELECT concat(first_name, ' ', last_name) AS customer,
       COUNT(rental_id) AS volume
FROM customer
LEFT JOIN rental USING(customer_id)
GROUP BY customer
ORDER BY volume DESC
LIMIT 10
"]))

(defn top-customers-by-revenue
  "Returns the top-ten customers that spent more money."
  [conn]
  (jdbc/execute! conn ["
SELECT concat(first_name, ' ', last_name) AS customer,
       SUM(amount) AS total_spent
FROM customer
LEFT JOIN payment USING(customer_id)
GROUP BY customer
ORDER BY total_spent DESC
LIMIT 10
"]))
