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
  (GET "/:endpoint"
       [endpoint]
       (fn [{headers :headers}]
         (if-let [user (:authorization headers)]
           (response (logic/on-get user endpoint))
           "Invalid token")))
  (POST "/:endpoint"
        [endpoint]
        (fn [{headers :headers body :body}]
          (if-let [user (:authorization headers)]
            (logic/on-add user endpoint body)
            "Invalid token"))))

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
