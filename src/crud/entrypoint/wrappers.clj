(ns crud.entrypoint.wrappers
  (:require [compojure.core :refer :all]
            [compojure.response :refer [Renderable]]
            [ring.middleware.json :refer :all]
            [ring.util.response :refer [response status] :as outgoing]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :as ringd]
            [ring.middleware.cors :as cors]
            [clojure.walk :as walk])
  (:import com.mongodb.MongoException))

(defn wrap-database [handler]
  (fn [req]
    (try
      (handler req)
      (catch MongoException _
        (status {:body "Something went wrong"} 500)))))

(defn wrap-authorization [handler]
  (fn [req]
    (if (:authorization (:headers req))
      (handler (assoc req :token (:authorization (:headers req))))
      (status {:body {:message "Invalid token"} :rest req} 401))))

(defn wrap-keywords
  ([handler]
   (wrap-keywords handler {}))
  ([handler _]
   (fn [req]
     (handler (walk/keywordize-keys req)))))

(defn wrap-cors [handler]
  (cors/wrap-cors
   handler
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:get :put :post :delete]))

(defn wrap-defaults [handler]
  (ringd/wrap-defaults handler (assoc ringd/api-defaults :security {:anti-forgery false})))

(def meta-wrappers
  #(-> %
       wrap-json-response
       wrap-database
       wrap-keywords
       wrap-cors
       wrap-json-body
       wrap-defaults))

(def crud-wrappers
  #(-> %
       wrap-database
       wrap-authorization
       wrap-keywords
       wrap-cors
       wrap-json-body
       wrap-json-response
       wrap-defaults))

(extend-protocol Renderable
  clojure.lang.PersistentVector
  (render
    [[data {message :message status :status}] _]
    (if data
      (outgoing/response data)
      (outgoing/status {:body message} status))))

