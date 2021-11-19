(ns monmon.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

;; Refridgerator
(comment
  (defn has-endpoint [db id]
    (mc/find-maps db "endpoints" {:_id id}))

  (defn make-version
    ([methods data]
     (make-version (System/currentTimeMillis) methods data))
    ([timestamp methods data]
     {(keyword (str timestamp))
      {:id (ObjectId.)
       :methods methods
       :data [data]}}))

  (def all-methods ["get" "get-id" "post" "put" "delete"])

  (defn create-endpoint [db user name methods data]
    (mc/insert
     db
     "endpoints"
     (let [timestamp (System/currentTimeMillis)]
       {:keys []
        :user user
        :endpoints
        {(keyword name)
         {:created timestamp
          :id (ObjectId.)
          :versions
          (make-version timestamp methods data)}}})))

  (defn add-endpoint-version [db endpoint name methods data]
    (mc/update-by-id db "endpoints" endpoint {(keyword (str name)) {$push {:versions (make-version methods data)} }})))

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
     {:user u :name endpoint :data data})))

(comment
  (update-endpoint id "pets" {:name "Peter" :hair "hard"})
  (get-data user-id "pets"))
