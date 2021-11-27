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
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.walk :as walk]))

(defroutes app-routes
  ;; TODO: add abastraction layer
  (GET "/:endpoint"
       [endpoint]
       (fn [{headers :headers}]
         (let [user (get headers "authorization")]
           (if user
             (response (logic/get-data user endpoint))
             "Invalid token"))))
  (POST "/:endpoint"
        [endpoint]
        (fn [{headers :headers body :body}]
          (let [user (get headers "authorization")]
            (if user
              (logic/add-endpoint user endpoint body)
              "Invalid token")))))

(def entrypoint
  (-> app-routes
      ;; TODO: Parse body
      wrap-cors
      wrap-json-body
      ;; Only use keywords (performance?)
      walk/keywordize-keys
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


;; TODO:
;; https://practical.li/clojure-webapps/projects/status-monitor-deps/debugging-requests.html

