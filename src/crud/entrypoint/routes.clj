(ns crud.entrypoint.routes
  (:require  [compojure.core :refer :all]
             [ring.util.response :refer [response status] :as outgoing]
             [crud.persistence.mongo :refer [db]]
             [crud.logic.core :as logic]))

;; TODO: Fin way to handle in a wrapper
(defn handle-work [[data {message status}]]
  (if data
    (outgoing/response data)
    (outgoing/status {:body message} status)))

;; TOOD: Add context for token
(defroutes app-routes
  (context
   "/:endpoint"
   [endpoint]
   (GET "/:id" [id] (fn [{user :token}] (handle-work (logic/on-get-id db user endpoint id))))
   (GET "/" [] (fn [{user :token}] (handle-work (logic/on-get db user endpoint))))
   (POST "/" [] (fn [{user :token body :body}] (handle-work (logic/on-post db user endpoint body))))
   (PUT "/:id" [id] (fn [{user :token body :body}] (handle-work (logic/on-put db user endpoint id body))))
   (DELETE "/:id" [id] (fn [{user :token body :body}] (handle-work (logic/on-delete-by-id db user endpoint id))))))
