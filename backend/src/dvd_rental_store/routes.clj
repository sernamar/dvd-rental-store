(ns dvd-rental-store.routes
  (:require [dvd-rental-store.db :refer [conn revenue revenue-by-month revenue-by-week revenue-by-day revenue-by-store
                                         top-ten-films-by-volume top-ten-films-by-revenue]]
            [compojure.core :refer [routes context GET]]
            [compojure.route :as route]
            [ring.util.response :refer [response not-found]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(defn revenue-routes [conn]
  (context "/revenue" []
           (GET "/" [] (response (revenue conn)))
           (GET "/by-month" [] (response (revenue-by-month conn)))
           (GET "/by-week" [] (response (revenue-by-week conn)))
           (GET "/by-day" [] (response (revenue-by-day conn)))
           (GET "/by-store" [] (response (revenue-by-store conn)))))

(defn film-routes [conn]
  (context "/film" []
           (GET "/most-popular" [] (response (top-ten-films-by-volume conn)))
           (GET "/most-revenue" [] (response (top-ten-films-by-revenue conn)))))

(defn all-routes [conn]
  (routes
   (revenue-routes conn)
   (film-routes conn)
   (route/not-found (not-found "Not Found"))))

(def app
  (-> conn
      all-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response))
