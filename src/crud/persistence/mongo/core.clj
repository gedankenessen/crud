(ns crud.persistence.mongo.core
  (:require [monger.core :as mg]
            [monger.credentials :as mcred]
            [crud.persistence.protocol :refer [Persistence]]
            [crud.persistence.mongo.crud :refer :all]
            [crud.persistence.mongo.user :refer :all]
            [crud.persistence.mongo.meta :refer :all]))

(defn connect
  "Takes `host`,`post`, `user`, `db` and `pw` from `config` and uses those to append a `conn` to config"
  [config]
  (assoc
   config
   :conn
   (mg/connect-with-credentials
    (:host config)
    (:port config)
    (mcred/create
     (:user config)
     ;; DO NOT USED `:db` HERE THIS IS ABOUT THE AUTHSTORE!
     (:auth-db config)
     (:pw config)))))

(defrecord Mongo-Driver [host port db conn]
  Persistence
  ;; setup
  (connect [config] (connect config))
  ;; crud functions
  (get-data [config userId endpoint] (get-data config userId endpoint))
  (get-data-by-id [config userId endpoint dataId] (get-data-by-id config userId endpoint dataId))
  (get-data-last [config userId endpoint] (get-data-last config userId endpoint))
  (add-endpoint [config userId endpoint new-data] (add-endpoint config userId endpoint new-data))
  (add-data [config userId endpoint new-data] (add-data config userId endpoint new-data))
  (add-version [config userId endpoint new-data] (add-version config userId endpoint new-data))
  (delete-data-by-id [config userId endpoint id] (delete-data-by-id config userId endpoint id))
  (update-data-by-id [config userId endpoint id new-data] (update-data-by-id config userId endpoint id new-data))
  ;; user functions
  (get-user-by-email [config email] (get-user-by-email config email))
  (get-user-by-id [config userId] (get-user-by-id config userId))
  (add-user [config new-data] (add-user config new-data))
  (update-user [config userId new-data] (update-user config userId new-data))
  (delete-user [config userId] (delete-user config userId))
  ;; meta functions
  (get-endpoints [db userId] (get-endpoints db userId))
  (get-endpoint-by-id [db userId endpointId] (get-endpoint-by-id db userId endpointId))
  (delete-endpoint-by-id [db userId endpointId] (delete-endpoint-by-id db userId endpointId))
  (delete-endpoints-by-userId [db userId] (delete-endpoints-by-userId db userId))
  (update-endpoint-by-id [db userId endpointId new-data] (update-endpoint-by-id db userId endpointId new-data)))
