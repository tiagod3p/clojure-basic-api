(ns basic-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route])
  (:gen-class))

(defn handler [request]
  (response {:body {:statusCode "200" :message "Home Page"}}))

(defn route_not_found [request]
  (response {:body {:statusCode "404" :message "Page not found"}}))

(def app
  (wrap-json-response handler))

(defroutes index
  (GET "/test" [] app)
  (route/not-found (wrap-json-response route_not_found)))

(defn -main
  []
  (run-jetty index {:port 3333}))

