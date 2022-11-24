(ns crud.entrypoint
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer :all]
            [ring.util.response :refer [response status] :as outgoing]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.walk :as walk]
            [crud.persistence.mongo :refer [db]]
            [crud.logic :as logic])
  (:import com.mongodb.MongoException))

(defn handle-work [[data {message status}]]
  (if data
    (outgoing/response data)
    (outgoing/status {:body message} status)))

(defroutes app-routes
  (context
   "/:endpoint"
   [endpoint]
   (GET "/:id" [id] (fn [{user :token}] (handle-work (logic/on-get-id db user endpoint id))))
   (GET "/" [] (fn [{user :token}] (handle-work (logic/on-get db user endpoint))))
   (POST "/" [] (fn [{user :token body :body}] (handle-work (logic/on-post db user endpoint body))))
   (PUT "/:id" [id] (fn [{user :token body :body}] (handle-work (logic/on-put db user endpoint id body))))
   (DELETE "/:id" [id] (fn [{user :token body :body}] (handle-work (logic/on-delete-by-id db user endpoint id))))))

(defn wrap-reject-database-errors [handler]
  (fn [req]
    (try
      (handler req)
      (catch MongoException _
        (status {:body "Something went wrong"} 500)))))

(defn wrap-reject-no-header [handler]
  (fn [req]
    (if (:authorization (:headers req))
      (handler (assoc req :token (:authorization (:headers req))))
      (status {:body {:message "Invalid token"} :rest req} 401))))

(defn wrap-request-keywords
  ([handler]
   (wrap-request-keywords handler {}))
  ([handler _]
   (fn [req]
     (handler (walk/keywordize-keys req)))))

(def entrypoint
  (-> app-routes
      wrap-database-errors
      wrap-reject-no-header
      wrap-request-keywords
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])
      wrap-json-body
      wrap-json-response
      (wrap-defaults (assoc api-defaults :security {:anti-forgery false}))))

(defn start-server [port]
  (println (str "Starting server at http:/127.0.0.1:" port "  ..."))
  (server/run-server entrypoint {:port port :legacy-return-value? false}))

(comment
  (def server (atom (start-server 3004)))
  @server
  (server/server-stop! @server))
