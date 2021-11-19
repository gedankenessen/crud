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
            [monmon.logic :as logic])
  (:gen-class))

(defroutes app-routes
  ;;(GET "/:endpoint" [endpoint] (logic/get-data "" endpoint))
  (GET "/pets"
       []
       (fn [{headers :headers}]
         (let [user (get headers "authorization")]
           (if user
             ;; TODO: don't hardcode endpoint
             (response (logic/get-data user "pets"))
             ("User could not be found"))))))

(defn start-server [port]
  ;; https://github.com/ring-clojure/ring-json
  (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
  (println (str "Running server at http:/127.0.0.1:" port)))

(comment
  (start-server 3004))
