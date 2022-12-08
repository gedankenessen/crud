(ns crud.entrypoint.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response status] :as outgoing]
            [ring.middleware.reload :refer [wrap-reload]]
            [crud.entrypoint.routes :refer :all]
            [crud.entrypoint.wrappers :refer [wrappers wrap-authorization]]))

(defroutes app-routes
  (wrappers
   (routes
    user-routes
    signup-routes
    meta-routes
    crud-routes)))

(def reloaded-app
  (wrap-reload #'app-routes))

(defn start-server [{port :port  app :app, :or {port 3004 app reloaded-app}}]
  (println (str "Starting server at http:/127.0.0.1:" port " at: " (System/currentTimeMillis)))
  (server/run-server app {:port port :legacy-return-value? false}))

(comment
  (def server (atom (start-server {:port 3004})))
  @server
  (server/server-stop! @server))
