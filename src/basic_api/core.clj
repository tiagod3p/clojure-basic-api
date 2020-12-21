(ns basic-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [monger.core :as mg]
            [monger.collection :as mc])
 (:gen-class))

(def uri "mongodb://tiago:DF0aYEH4GMoc7nCn@githubcluster-shard-00-00.tgupk.mongodb.net:27017,githubcluster-shard-00-01.tgupk.mongodb.net:27017,githubcluster-shard-00-02.tgupk.mongodb.net:27017/ecommerce-api?ssl=true&replicaSet=atlas-79tl7a-shard-0&authSource=admin&retryWrites=true&w=majority&readPreference=secondary")

(def connection (mg/connect-via-uri uri))
(def conn (:conn connection))
(def db (:db connection))

(defn remove-_id-from-mongo-map [object]
  (dissoc object :_id))

(defn insert-product [product]
  (mc/insert db "products" product))

(defn get-all-products []
  (mc/find-maps db "products"))

(defn handler-get-all-products [request]
  (let [products (get-all-products)]
    (response {:statusCode 200 :body (map remove-_id-from-mongo-map products)})))

(defn handler-insert-product [request]
  (insert-product (get-in request[:body]))
  (response {:statusCode "200" :body (get-in request [:body])}))

(defn route-not-found [request]
  (response {:statusCode 404 :body "Page not found."}))

(def products  (wrap-json-response handler-get-all-products))
(def post-product (wrap-json-response (wrap-json-body handler-insert-product {:keywords? true :bigdecimals? true})))
(def not-found (wrap-json-response route-not-found))

(defroutes index
  (GET "/products" [] products)
  (POST "/products" request post-product)
  (route/not-found not-found))

(defn -main
  []
  (run-jetty index {:port 3333}))

