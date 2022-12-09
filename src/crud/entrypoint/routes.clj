(ns crud.entrypoint.routes
  (:require  [compojure.core :refer :all]
             [ring.util.response :refer [response status] :as outgoing]
             [crud.entrypoint.tokens :refer [sign-token config]]
             [crud.entrypoint.wrappers :refer [wrap-authorization]]
             [crud.logic.core :as logic]
             [crud.logic.user :as user]
             [crud.logic.meta :as meta]))

(defn build-sign-up-routes [{db :db}]
  (defroutes signup-routes
    "Routes for account sign-up. Does not require an authorization header to be present."
    (context
     "/user" {body :body}
     (POST "/register" [] (fn [_] (user/register db body)))
     (POST "/login" [] (fn [_] (user/login db body))))))

(defn build-user-routes [{db :db}]
  (defroutes user-routes
    "Routes for user (token) related actions. Requires authorization header."
    (wrap-routes
     (context
      "/user" []
      (GET "/" [] (fn [{userId :token}] (user/details db userId)))
      (PUT "/" [] (fn [{userId :token body :body}] (user/details db userId body)))
      (DELETE "/" [] (fn [{userId :token body :body}] (user/delete db userId body)))
      (POST "/token" [] (fn [{userId :token body :body}] (sign-token (:id body) config))))
     wrap-authorization)))

(defn build-meta-routes [{db :db}]
  (defroutes meta-routes
    "Meta routes to work with the endpoints themselves. Requires authorization header."
    (wrap-routes
     (context
      "/meta" []
      (GET "/" [] (fn [{userId :token}] (meta/get-endpoints-by-userId db userId)))
      (DELETE "/" [] (fn [{userId :token}] (meta/delete-endpoints-by-userId db userId)))
      (context
       "/:id" [endpointId]
       (DELETE "/" [] (fn [{userId :token}] (meta/delete-endpoint-by-id db userId endpointId)))
       (GET "/" [] (fn [{userId :token}] (meta/get-endpoint-by-id db userId endpointId)))
       (PUT "/" [] (fn [{userId :token body :body}] (meta/update-endpoint-by-id db userId endpointId body)))))
     wrap-authorization)))

;; TODO: Refactor from `/endpoints` to `/build` or `/crud` ?
(defn build-crud-routes [{db :db}]
  (defroutes crud-routes
    "Business logic routes. Heart of crud. Requires authorization header."
    (wrap-routes
     (context
      "/endpoints/:endpoint" [endpoint]
      (GET "/:id" [id] (fn [{user :token}] (logic/on-get-id db user endpoint id)))
      (GET "/" [] (fn [{user :token}] (logic/on-get db user endpoint)))
      (POST "/" [] (fn [{user :token body :body}] (logic/on-post db user endpoint body)))
      (PUT "/:id" [id] (fn [{user :token body :body}] (logic/on-put db user endpoint id body)))
      (DELETE "/:id" [id] (fn [{user :token body :body}] (logic/on-delete-by-id db user endpoint id))))
     wrap-authorization)))
