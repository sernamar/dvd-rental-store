(ns dvd-rental-store.routes
  (:require [dvd-rental-store.db :refer [conn total-revenue total-revenue-by-month
                                         total-revenue-by-week total-revenue-by-day]]
            [compojure.core :refer [routes context GET]]
            [compojure.route :as route]
            [ring.util.response :refer [response not-found]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(defn db-routes [conn]
  (context "/revenue" []
           (GET "/" [] (response (total-revenue conn)))
           (GET "/by-month" [] (response (total-revenue-by-month conn)))
           (GET "/by-week" [] (response (total-revenue-by-week conn)))
           (GET "/by-day" [] (response (total-revenue-by-day conn)))))

(defn all-routes [conn]
  (routes
   (db-routes conn)
   (route/not-found (not-found "Not Found"))))

(def app
  (-> conn
      all-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response))
