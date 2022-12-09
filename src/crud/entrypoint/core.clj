(ns crud.entrypoint.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response status] :as outgoing]
            [ring.middleware.reload :refer [wrap-reload]]
            [crud.persistence.protocol :as persistence]
            [crud.entrypoint.routes :refer :all]
            [crud.entrypoint.wrappers :refer [wrappers wrap-authorization]]))

(defn build-routes [config]
  (let [config (assoc config :db (persistence/connect (:db config)))]
    (defroutes app-routes
      (wrappers
       (routes
        (build-user-routes config)
        (build-sign-up-routes config)
        (build-meta-routes config)
        (build-crud-routes config))))))

(defn reloaded-app [config]
  (wrap-reload #(build-routes config)))

(defn start-server [config]
  (println
   (str
    "Starting server at http:/127.0.0.1:"
    (-> config :api :port)
    " at: "
    (System/currentTimeMillis)))
  (server/run-server
   (if (= :prod (-> config :api :env))
     (build-routes config)
     (reloaded-app config))
   {:port (-> config :api :port) :legacy-return-value? false}))

