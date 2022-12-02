(ns crud.entrypoint.routes
  (:require  [compojure.core :refer :all]
             [ring.util.response :refer [response status] :as outgoing]
             [crud.persistence.mongo :refer [db]]
             [crud.entrypoint.tokens :refer [sign-token config]]
             [crud.logic.core :as logic]
             [crud.logic.user :as user]
             [crud.logic.meta :as meta]))

(defroutes signup-routes
  "Routes for account sign-up. Does not require an authorization header to be present."
  (context
   "/user" []
   (POST "/join" [] (fn [{body :body}] (user/join body)))
   (POST "/login" [] (fn [{body :body}] (user/login body)))))

(defroutes user-routes
  "Routes for user (token) related actions. Requires authorization header."
  (context
   "/user" []
   (GET "/" [] (fn [{token :token}] (user/details token)))
   (PUT "/" [] (fn [{token :token body :body}] (user/details token body)))
   (DELETE "/" [] (fn [{token :token body :body}] (user/delete token body)))
   (POST "/token" [] (fn [{body :body}] (sign-token (:id body) config)))))

(defroutes meta-routes
  "Meta routes to work with the endpoints themselves. Requires authorization header."
  (context
   "/meta/:id" [id]
   (DELETE "/" [] (fn [_] (meta/delete-endpoint id)))
   (GET "/" [] (fn [_] (meta/get-endpoint id)))
   (PUT "/" [] (fn [{body :body}] (meta/update-endpoint id body)))))

;; TODO: Refactor from `/endpoints` to `/build` or `/crud` ?
(defroutes crud-routes
  "Business logic routes. Heart of crud. Requires authorization header."
  (context
   "/endpoints/:endpoint"
   [endpoint]
   (GET "/:id" [id] (fn [{user :token}] (logic/on-get-id db user endpoint id)))
   (GET "/" [] (fn [{user :token}] (logic/on-get db user endpoint)))
   (POST "/" [] (fn [{user :token body :body}] (logic/on-post db user endpoint body)))
   (PUT "/:id" [id] (fn [{user :token body :body}] (logic/on-put db user endpoint id body)))
   (DELETE "/:id" [id] (fn [{user :token body :body}] (logic/on-delete-by-id db user endpoint id)))))
