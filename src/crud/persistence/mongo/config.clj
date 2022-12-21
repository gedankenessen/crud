(ns crud.persistence.mongo.config
  (:require [crud.persistence.mongo.core :refer [map->Mongo-Driver]]
            [crud.utility :refer [build-config]]))

(def _config
  "Default values for MongoDB"
  {:host "localhost"
   :port 27017
   :db "crud-testing"
   :should-auth false
   :user "root"
   :auth-db "admin"
   :pw "example"
   :conn nil})

(def config
  "Default config for MongoDB"
  (map->Mongo-Driver _config))

(def relevant-keys [:host :port :db :should-auth :user :auth-db :pw])

(defn get-env-config
  "Map env variables to config"
  []
  {:host (System/getenv "CRUD_MONGO_HOST")
   :post (System/getenv "CRUD_MONGO_PORT")
   :db (System/getenv "CRUD_MONGO_DB")
   :should-auth (System/getenv "CRUD_MONGO_SHOULD_AUTH")
   :user (System/getenv "CRUD_MONGO_USER")
   :auth-db (System/getenv "CRUD_MONGO_AUTH_DB")
   :pw (System/getenv "CRUD_MONGO_PASSWORT")})

(def get-cli-options
  [["-U" "--mongo-user USER" "MongoDB username"
    :parse-fn #(str %)]
   ["-W" "--mongo-password password" "MongoDB password"
    :parse-fn #(str %)]
   ["-C" "--mongo-coll DB" "Name of collection that crud should use"
    :parse-fn #(str %)]
   ["-L" "--mongo-url URL" "URL that MongoDB is running on"
    :parse-fn #(str %)]
   ["-P" "--mongo-port PORT" "The port that MongoDB is running on"
    :parse-fn #(Integer/parseInt %)]
   ["-a" "--mongo-should-auth BOOLEAN" "Should crud auth with db?"
    :parse-fn #(read-string %)]
   ["-A" "--mongo-auth-db STRING" "Name of auth collection"
    :parse-fn #(read-string %)]])

(defn get-args-config
  "Maps command-line args to config"
  [{host :mongo-host
    port :mongo-port
    user :mongo-user
    should-auth? :mongo-should-auth
    pw :mongo-password
    auth-db :mongo-auth-db
    db :mongo-coll}]
  {:host host
   :port port
   :should-auth should-auth?
   :user user
   :pw pw
   :auth-db auth-db
   :db db})

(defn get-config
  "Build config from defaults, env-variables and cli arguments"
  [args]
  ;; Important to map to Record, so Persistence protocol is working!
  (map->Mongo-Driver
   (build-config
    relevant-keys
    _config
    (get-env-config)
    (get-args-config args))))

