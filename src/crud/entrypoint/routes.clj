(ns crud.entrypoint.routes
  (:require  [compojure.core :refer :all]
             [ring.util.response :refer [response status] :as outgoing]
             [crud.persistence.mongo.core :refer [db]]
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
  (wrap-routes
   (context
    "/user" []
    (GET "/" [] (fn [{token :token}] (user/details db token)))
    (PUT "/" [] (fn [{token :token body :body}] (user/details db token body)))
    (DELETE "/" [] (fn [{token :token body :body}] (user/delete db token body)))
    (POST "/token" [] (fn [{body :body}] (sign-token (:id body) config))))
   wrap-authorization))

(defroutes meta-routes
  "Meta routes to work with the endpoints themselves. Requires authorization header."
  (wrap-routes
   (context
    "/meta/:id" [id]
    (DELETE "/" [] (fn [_] (meta/delete-endpoint db id)))
    (GET "/" [] (fn [_] (meta/get-endpoint db id)))
    (PUT "/" [] (fn [{body :body}] (meta/update-endpoint db id body))))
   wrap-authorization))

;; TODO: Refactor from `/endpoints` to `/build` or `/crud` ?
(defroutes crud-routes
  "Business logic routes. Heart of crud. Requires authorization header."
  (wrap-routes
   (context
    "/endpoints/:endpoint"
    [endpoint]
    (GET "/:id" [id] (fn [{user :token}] (logic/on-get-id db user endpoint id)))
    (GET "/" [] (fn [{user :token}] (logic/on-get db user endpoint)))
    (POST "/" [] (fn [{user :token body :body}] (logic/on-post db user endpoint body)))
    (PUT "/:id" [id] (fn [{user :token body :body}] (logic/on-put db user endpoint id body)))
    (DELETE "/:id" [id] (fn [{user :token body :body}] (logic/on-delete-by-id db user endpoint id))))
   wrap-authorization))
