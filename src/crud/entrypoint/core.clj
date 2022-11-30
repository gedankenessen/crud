(ns crud.entrypoint.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response status] :as outgoing]
            [crud.entrypoint.routes :refer :all]
            [crud.entrypoint.wrappers
             :refer [wrap-database
                     wrap-cors
                     wrap-authorization
                     wrap-keywords]]))

(def entrypoint
  (-> app-routes
      wrap-database
      wrap-authorization
      wrap-keywords
      wrap-cors
      wrap-json-body
      wrap-json-response
      ;; TODO: Remove :anti-forgery (also look if wrap-defaults is needed)
      (wrap-defaults (assoc api-defaults :security {:anti-forgery false}))))

(defn start-server [port]
  (println (str "Starting server at http:/127.0.0.1:" port "  ..."))
  (server/run-server entrypoint {:port port :legacy-return-value? false}))

(comment
  (def server (atom (start-server 3004)))
  @server
  (server/server-stop! @server))
