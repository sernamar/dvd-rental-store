(ns dvd-rental-store.routes
  (:require [compojure.core :refer [context defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [not-found]]))

(defroutes routes
  ;; test routes
  (GET "/" [] "Hello, World!")

  ;; non-existing routes
  (route/not-found (not-found "Not Found")))

(def app
  (-> routes
      (wrap-json-body {:keywords? true})
      wrap-json-response))
