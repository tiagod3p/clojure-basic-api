(ns basic-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route])
  (:gen-class))

(defroutes index
  (GET "/" [] "TEST")
  (route/not-found "404 not found"))

(defn -main
  []
  (run-jetty index {:port 3333}))

