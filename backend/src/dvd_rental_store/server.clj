(ns dvd-rental-store.server
  (:require [dvd-rental-store.routes :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.tools.logging :as log])
  (:gen-class))

(def server (atom nil))
(def port 8080)

(defn start []
  (let [port (or port 8080)]
    (reset! server (run-jetty #'app {:port port :join? false}))
    (log/info (str "Server running on http://localhost/" port))))

(defn stop []
  (.stop @server)
  (log/info "Server stopped."))

(defn reset-server! []
  (when @server
    (stop))
  (start))

(defn -main [& args]
  (when-not @server
    (start)))
