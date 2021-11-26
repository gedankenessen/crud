(ns monmon.entrypoint
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer :all]
            [ring.util.response :refer [response]]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [monmon.logic :as logic]
            [ring.middleware.cors :refer [wrap-cors]]))

(defroutes app-routes
  ;; TODO: add abastraction layer
  (GET "/:endpoint"
       [endpoint]
       (fn [{headers :headers}]
         (let [user (get headers "authorization")]
           (if user
             ;; TODO: don't hardcode endpoint
             (response (logic/get-data user endpoint))
             ("User could not be found")))))
  (POST "/:endpoint"
        [endpoint]
        (fn [{headers :headers body :body}]
          (println body)
          "Thank you for posting"
          ;; (let [user (get headers "authorization")]
          ;;   (if user
          ;;     (logic/add-endpoint user endpoint body)
          ;;     "User could not be found"))
          )))

(def entrypoint
  (-> app-routes
      ;; TODO: Parse body
      wrap-cors
      ;;(wrap-json-params)
      wrap-json-body
      wrap-json-response
      (wrap-defaults (assoc api-defaults :security {:anti-forgery false}))))

(defn start-server [port]
  ;; https://github.com/ring-clojure/ring-json
  (println (str "Starting server at http:/127.0.0.1:" port "  ..."))
  (server/run-server entrypoint {:port port :legacy-return-value? false}))

(comment
  ;; Start server
  (def server (atom (start-server 3004)))
  ;; Inspect server objc
  @server
  ;; Stop server
  (server/server-stop! @server))

