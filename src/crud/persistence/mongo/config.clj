(ns crud.persistence.mongo.config
  (:require [crud.persistence.mongo.core :refer [map->Mongo-Driver]]
            [crud.utility :refer [build-config]]))

(def _config
  "Default config for MongoDB"
  {:host "localhost"
   :port 27017
   :db "crud-testing"
   :user "root"
   :auth-db "admin"
   :pw "example"
   :conn nil})

(def config
  "Default config for MongoDB"
  (map->Mongo-Driver _config))

(def relevant-keys [:host :port :db :user :auth-db :pw])

(defn get-env-config
  "Map env variables to config"
  []
  {:host (System/getenv "CRUD_MONGO_HOST")
   :post (System/getenv "CRUD_MONGO_PORT")
   :db (System/getenv "CRUD_MONGO_DB")
   :user (System/getenv "CRUD_MONGO_USER")
   :auth-db (System/getenv "CRUD_MONGO_AUTH_DB")
   :pw (System/getenv "CRUD_MONGO_PASSWORT")})

(defn get-args-config
  "Maps command-line args to config"
  [{host :mongo-host
    port :mongo-port
    user :mongo-user
    pw :mongo-password
    auth-db :mongo-auth-db
    db :mongo-db}]
  {:host host
   :port port
   :user user
   :pw pw
   :auth-db auth-db
   :db db})

(defn get-config [args]
  (map->Mongo-Driver
   (build-config
    relevant-keys
    _config
    (get-env-config)
    (get-args-config args))))

