(ns crud.entrypoint.routes
  (:require  [compojure.core :refer :all]
             [ring.util.response :refer [response status] :as outgoing]
             [crud.persistence.mongo :refer [db]]
             [crud.entrypoint.tokens :refer [sign-token config]]
             [crud.logic.core :as logic]))

(defroutes meta-routes
  (context
   "/meta/token" []
   ;; TODO: Move from /:id to body
   (POST "/:id" [id] (fn [_] (sign-token id config)))))

(defroutes crud-routes
  (context
   "/endpoints/:endpoint"
   [endpoint]
   (GET "/:id" [id] (fn [{user :token}] (logic/on-get-id db user endpoint id)))
   (GET "/" [] (fn [{user :token}] (logic/on-get db user endpoint)))
   (POST "/" [] (fn [{user :token body :body}] (logic/on-post db user endpoint body)))
   (PUT "/:id" [id] (fn [{user :token body :body}] (logic/on-put db user endpoint id body)))
   (DELETE "/:id" [id] (fn [{user :token body :body}] (logic/on-delete-by-id db user endpoint id)))))
