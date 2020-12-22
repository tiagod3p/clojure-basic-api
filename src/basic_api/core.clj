(ns basic-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [GET POST PUT defroutes]]
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
  (mc/insert-and-return db "products" product))

(defn get-all-products []
  (mc/find-maps db "products"))

(defn get-product-by-id [productId]
  (mc/find-one-as-map db "products" {:productId (read-string productId)}))

(defn update-product [productId body]
  (mc/update db "products" {:productId (read-string productId)} {:$set body} {:multi false}))

(defn handler-get-all-products [request]
  (let [products (get-all-products)]
    (response {:statusCode 200 :body (map remove-_id-from-mongo-map products)})))

(defn handler-get-product-by-id [request]
  (let [productId (get-in request [:params :productId])
        product (get-product-by-id productId)]
    (response {:statusCode 200 :body (dissoc product :_id)})))

(defn handler-update-product [request]
  (let [productId (get-in request [:params :productId])
        body (get-in request [:body])]
    (update-product productId body)
    (response {:statusCode 200 :body "Product updated."})))

(defn handler-insert-product [request]
  (let [product (insert-product (get-in request [:body]))]
    (response {:statusCode 200 :body product})))

(defn route-not-found [request]
  (response {:statusCode 404 :body "Page not found."}))

(def get-products  (wrap-json-response handler-get-all-products))
(def get-product (wrap-json-response (wrap-json-params handler-get-product-by-id)))
(def post-product (wrap-json-response (wrap-json-body handler-insert-product {:keywords? true})))
(def put-product (wrap-json-response (wrap-json-body handler-update-product {:keywords? true})))
(def not-found (wrap-json-response route-not-found))

(defroutes index
  (GET "/products" [] get-products)
  (GET "/products/:productId" request get-product)
  (PUT "/products/:productId" request put-product)
  (POST "/products" request post-product)
  (route/not-found not-found))

(defn -main
  []
  (run-jetty index {:port 3333}))

