(ns dvd-rental-store.routes
  (:require [dvd-rental-store.db :as db]
            [compojure.core :refer [routes context GET]]
            [compojure.route :as route]
            [ring.util.response :refer [response not-found]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(defn revenue-routes [conn]
  (context "/revenue" []
           (GET "/" [] (response (db/revenue conn)))
           (GET "/by-month" [] (response (db/revenue-by-month conn)))
           (GET "/by-week" [] (response (db/revenue-by-week conn)))
           (GET "/by-day" [] (response (db/revenue-by-day conn)))
           (GET "/by-store" [] (response (db/revenue-by-store conn)))))

(defn volume-routes [conn]
  (context "/volume" []
           (GET "/" [] (response (db/volume conn)))
           (GET "/by-store" [] (response (db/volume-by-store conn)))
           (GET "/by-month-and-store" [] (response (db/volume-by-month-and-store conn)))))

(defn film-routes [conn]
  (context "/film" []
           (GET "/most-popular" [] (response (db/top-ten-films-by-volume conn)))
           (GET "/most-revenue" [] (response (db/top-ten-films-by-revenue conn)))
           (GET "/number-by-category" [] (response (db/number-of-films-by-category conn)))))

(defn category-routes [conn]
  (context "/category" []
           (GET "/by-volume" [] (response (db/top-categories-by-volume conn)))
           (GET "/by-revenue" [] (response (db/top-categories-by-revenue conn)))))

(defn customer-routes [conn]
  (context "/customer" []
           (GET "/by-volume" [] (response (db/top-customers-by-volume conn)))
           (GET "/by-revenue" [] (response (db/top-customers-by-revenue conn)))))

(defn all-routes [conn]
  (routes
   (revenue-routes conn)
   (volume-routes conn)
   (film-routes conn)
   (category-routes conn)
   (customer-routes conn)
   (route/not-found (not-found "Not Found"))))

(def app
  (-> db/conn
      all-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response))
