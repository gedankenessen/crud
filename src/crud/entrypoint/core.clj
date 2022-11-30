(ns crud.entrypoint.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response status] :as outgoing]
            [crud.entrypoint.routes :refer :all]
            [crud.entrypoint.wrappers :refer [meta-wrappers crud-wrappers]]))

(def app-routes
  (routes
   (-> meta-routes
       meta-wrappers)
   (-> crud-routes
       crud-wrappers)))

(defn start-server [port]
  (println (str "Starting server at http:/127.0.0.1:" port "  ..."))
  (server/run-server app-routes {:port port :legacy-return-value? false}))

(comment
  (def server (atom (start-server 3004)))
  @server
  (server/server-stop! @server))
