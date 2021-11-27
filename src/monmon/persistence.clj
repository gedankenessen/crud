(ns monmon.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(def conn (delay (mg/connect)))

(defn get-endpoint [user endpoint]
  (let [db (mg/get-db @conn "crud")]
    (mc/find-one-as-map db "endpoints" {:user (ObjectId. user) :name endpoint})))

(defn update-endpoint [user endpoint data]
  (let [db (mg/get-db @conn "crud")
        u (ObjectId. user)]
    (mc/upsert
     db
     "endpoints"
     {:user u}
     {:user u :name endpoint :data [data]}))
  "POST: Successfully added endpoint version")

(defn add-to-endpoint [user endpoint data]
  (let [db (mg/get-db @conn "crud")
        u (ObjectId. user)]
    (mc/update
     db
     "endpoints"
     {:user u :name endpoint}
     {$push {:data data}}))
  ;; TODO: Error handling+return id
  "POST: Successfully added data")

(defn get-endpoint-by-id [id]
  (let [db (mg/get-db @conn "crud")]
    (mc/find-by-id
     db
     "endpoints"
     (ObjectId. id))))