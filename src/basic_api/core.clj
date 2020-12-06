(ns basic-api.core
  (:use [ring.adapter.jetty]
        [compojure.core])
  (:require [compojure.route :as route])
  (:gen-class))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World"})

(defroutes index
  (GET "/" [] "TEST")
  (route/not-found "404 not found")  
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(run-jetty index {:port 3000})
