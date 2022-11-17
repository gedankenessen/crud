(ns crud.entrypoint
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer :all]
            [ring.util.response :refer [response status]]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.walk :as walk]
            [crud.glue :as glue]))

;; TODO: Find way to remove auth header stuff
(defroutes app-routes
  (context
   "/:endpoint"
   [endpoint]
   (GET "/:id"
        [id]
        (fn [{headers :headers}]
          (if-let [user (:authorization headers)]
            (glue/on-get-id user endpoint id)
            (status {:body {:message "Invalid token"}} 401))))
   (GET "/"
        []
        (fn [{headers :headers}]
          (if-let [user (:authorization headers)]
            (glue/on-get user endpoint)
            (status {:body {:message "Invalid token"}} 401))))
   (POST "/"
         []
         (fn [{headers :headers body :body}]
           (if-let [user (:authorization headers)]
             (glue/on-add user endpoint body)
             (status {:body {:message "Invalid token"}} 401))))
   (PUT "/:id"
        [id]
        (fn [{headers :headers body :body}]
          (if-let [user (:authorization headers)]
            (glue/on-put user endpoint id body)
            (status {:body {:message "Invalid token"}} 401))))
   (DELETE "/:id"
           [id]
           (fn [{headers :headers body :body}]
             (if-let [user (:authorization headers)]
               (glue/on-delete-by-id user endpoint id)
               (status {:body {:message "Invalid token"}} 401))))))

(defn wrap-request-keywords
  ([handler]
   (wrap-request-keywords handler {}))
  ([handler _]
   (fn [req]
     (handler (walk/keywordize-keys req)))))

(def entrypoint
  (-> app-routes
      wrap-request-keywords
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])
      wrap-json-body
      wrap-json-response
      ;; TODO: reject with no auth header
      (wrap-defaults (assoc api-defaults :security {:anti-forgery false}))))

(defn start-server [port]
  (println (str "Starting server at http:/127.0.0.1:" port "  ..."))
  (server/run-server entrypoint {:port port :legacy-return-value? false}))

(comment
  (def server (atom (start-server 3004)))
  @server
  (server/server-stop! @server))
