(ns crud.entrypoint.wrappers
  (:require [compojure.core :refer :all]
            [compojure.response :refer [Renderable]]
            [ring.middleware.json :refer :all]
            [ring.util.response :refer [response status] :as outgoing]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :as ringd]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.cors :as cors]
            [clojure.walk :as walk]
            [crud.entrypoint.tokens :refer [unsign-token]])
  (:import com.mongodb.MongoException))

(defn wrap-database [handler]
  (fn [req]
    (try
      (handler req)
      (catch IllegalArgumentException _
        (status {:body {:message "Malformed token"}} 403))
      (catch MongoException _
        (status {:body {:message "Something went wrong"}} 500)))))

(defn wrap-authorization [handler]
  (fn [req]
    (if (-> req :headers :authorization)
      (let [[token error] (unsign-token (-> req :headers :authorization))]
        (or error (handler (assoc req :token (:userId token)))))
      (status {:body {:message "Authorization token is missing"}} 401))))

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

(def wrap-defaults #(ringd/wrap-defaults % (assoc ringd/api-defaults :security {:anti-forgery false})))

(def wrappers
  #(-> %
       wrap-database
       wrap-json-response
       wrap-keywords
       wrap-json-body
       wrap-cors
       wrap-defaults
       wrap-content-type))

(extend-protocol Renderable
  clojure.lang.PersistentVector
  (render
    [[data {message :message status :status}] _]
    (if data
      (outgoing/response data)
      (outgoing/status {:body {:message message}} status))))

