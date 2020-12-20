(ns basic-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [monger.core :as mg]
            [monger.collection :as mc])
 (:gen-class))

;(let [uri "mongodb://tiago:KOybPSFH89hdcC6x@githubcluster-shard-00-00.tgupk.mongodb.net:27017,githubcluster-shard-00-01.tgupk.mongodb.net:27017,githubcluster-shard-00-02.tgupk.mongodb.net:27017/clojure_db?ssl=true&replicaSet=atlas-79tl7a-shard-0&authSource=admin&retryWrites=true&w=majority&readPreference=secondary"
      ;{:keys [conn db]} (mg/connect-via-uri uri)))

(def uri "mongodb://tiago:KOybPSFH89hdcC6x@githubcluster-shard-00-00.tgupk.mongodb.net:27017,githubcluster-shard-00-01.tgupk.mongodb.net:27017,githubcluster-shard-00-02.tgupk.mongodb.net:27017/clojure_db?ssl=true&replicaSet=atlas-79tl7a-shard-0&authSource=admin&retryWrites=true&w=majority&readPreference=secondary")

(def connection (mg/connect-via-uri uri))

(def conn (:conn connection))
(def db (:db connection))


(defn insert-at-mongo [body]
    (mc/insert db "test" body))

(defn handler [request]
  (response {:body {:statusCode "200" :message "Home Page" :connection (str connection)}}))

(defn route-not-found [request]
  (response {:body {:statusCode "404" :message "Page not found"}}))

(defn handler-request-body [request]
  (insert-at-mongo (get-in request[:body]))
  (response {:statusCode "200" :body (get-in request [:body])}))

(def app  (wrap-json-response handler))

(def post_request (wrap-json-response (wrap-json-body handler-request-body {:keywords? true :bigdecimals? true})))

(defroutes index
  (GET "/test" [] app)
  (POST "/test" request post_request)
  (route/not-found (wrap-json-response route-not-found)))

(defn -main
  []
  (run-jetty index {:port 3333}))

