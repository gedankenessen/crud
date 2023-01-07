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
            [cheshire.generate :refer [JSONable]]
            [crud.entrypoint.tokens :refer [unsign-token]]
            [cheshire.generate :refer [add-encoder encode-str remove-encoder]])
  (:import com.mongodb.MongoException org.bson.types.ObjectId))

(defn wrap-authorization [handler config]
  (fn [req]
    (let [[{token :userId} error] (unsign-token (-> req :headers :authorization) config)]
      (if error
        (status {:body (:message error)} (:status error))
        (handler (assoc req :token token))))))

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

(defn wrap-is-content-type
  "Check if the request `content-type` is set to `application/json` on :post and :put request."
  [handler]
  (fn [req]
    (let [method (-> req :request-method)
          type (-> req :headers :content-type)]
      (if (and (or (= method :post) (= method :put))
               (not (= type "application/json")))
        (status {:body "Incorrect Content-Type provided (should be application/json)"} 400)
        (handler req)))))

(def wrappers
  #(-> %
       wrap-is-content-type
       wrap-keywords
       wrap-json-body
       wrap-cors
       wrap-json-response
       wrap-defaults
       wrap-content-type))

;; Extend Compojure/Renderable to handle internal [data error] tuple.
(extend-protocol Renderable
  clojure.lang.PersistentVector
  (render
    [[data {message :message status :status}] _]
    (if (and (not message) (not status))
      (outgoing/response data)
      (outgoing/status {:body message} status))))

;; TODO: Find better placement
;; Extend Cheshire JSON converter to handle MongoDB IDs
(add-encoder org.bson.types.ObjectId encode-str)
