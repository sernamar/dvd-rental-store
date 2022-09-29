(ns dvd-rental-store.routes
  (:require [dvd-rental-store.db :refer [conn count-films]]
            [compojure.core :refer [routes context GET]]
            [compojure.route :as route]
            [ring.util.response :refer [response not-found]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(defn db-routes [conn]
  (context "/db" []
           (GET "/count-films" [name] (response (count-films conn)))))

(defn other-routes []
  (routes
   (GET "/" [] (response "Hello, World!"))
   (route/not-found (not-found "Not Found"))))

(defn all-routes [conn]
  (routes
   (db-routes conn)
   (other-routes)))

(def app
  (-> conn
      all-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response))
