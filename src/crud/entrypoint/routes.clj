(ns crud.entrypoint.routes
  (:require  [compojure.core :refer :all]
             [ring.util.response :refer [response status] :as outgoing]
             [crud.persistence.mongo :refer [db]]
             [crud.entrypoint.tokens :refer [sign-token config]]
             [crud.entrypoint.wrappers :refer [wrap-authorization]]
             [crud.logic.core :as logic]
             [crud.logic.user :as user]
             [crud.logic.meta :as meta]))

(defroutes signup-routes
  "Routes for account sign-up. Does not require an authorization header to be present."
  (context
   "/user" []
   (POST "/register" [] (fn [{body :body}] (user/register db body)))
   (POST "/login" [] (fn [{body :body}] (user/login db body)))))

(defroutes user-routes
  "Routes for user (token) related actions. Requires authorization header."
  (context
   "/user" []
   (GET "/" [] (wrap-authorization (fn [{token :token}] (user/details db token))))
   (PUT "/" [] (wrap-authorization (fn [{token :token body :body}] (user/details db token body))))
   (DELETE "/" [] (wrap-authorization (fn [{token :token body :body}] (user/delete db token body))))
   (POST "/token" [] (wrap-authorization (fn [{body :body}] (sign-token (:id body) config))))))

(defroutes meta-routes
  "Meta routes to work with the endpoints themselves. Requires authorization header."
  (context
   "/meta/:id" [id]
   (DELETE "/" [] (wrap-authorization (fn [_] (meta/delete-endpoint db id))))
   (GET "/" [] (wrap-authorization (fn [_] (meta/get-endpoint db id))))
   (PUT "/" [] (wrap-authorization (fn [{body :body}] (meta/update-endpoint db id body))))))

;; TODO: Refactor from `/endpoints` to `/build` or `/crud` ?
(defroutes crud-routes
  "Business logic routes. Heart of crud. Requires authorization header."
  (context
   "/endpoints/:endpoint"
   [endpoint]
   (GET "/:id" [id] (wrap-authorization (fn [{user :token}] (logic/on-get-id db user endpoint id))))
   (GET "/" [] (wrap-authorization (fn [{user :token}] (logic/on-get db user endpoint))))
   (POST "/" [] (wrap-authorization (fn [{user :token body :body}] (logic/on-post db user endpoint body))))
   (PUT "/:id" [id] (wrap-authorization (fn [{user :token body :body}] (logic/on-put db user endpoint id body))))
   (DELETE "/:id" [id] (wrap-authorization (fn [{user :token body :body}] (logic/on-delete-by-id db user endpoint id))))))
