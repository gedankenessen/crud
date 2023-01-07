(ns crud.persistence.mongo.meta
  (:require [clojure.set :refer [rename-keys]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?]]
            [crud.persistence.mongo.utility :refer [wrap-mongo-exception]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress] com.mongodb.MongoException))

(defn get-endpoints [config userId]
  (wrap-mongo-exception
   [(mc/find-maps
     (mg/get-db (:conn config) (:db config))
     "endpoints"
     {:userId (ObjectId. userId)}
     [:_id :userId :name :timestamp :data])
    nil]))

(defn get-endpoint-by-id [config userId endpointId]
  (wrap-mongo-exception
   [(mc/find-one-as-map
     (mg/get-db (:conn config) (:db config))
     "endpoints"
     {:_id (ObjectId. endpointId)
      :userId (ObjectId. userId)}
     [:_id :userId :name :timestamp :data])
    nil]))

(defn delete-endpoint-by-id [config userId endpointId]
  (wrap-mongo-exception
   (let [result
         (mc/remove
          (mg/get-db (:conn config) (:db config)) "endpoints"
          {:_id (ObjectId. endpointId)
           :userId (ObjectId. userId)})]
     (if (res/acknowledged? result)
       [{:_id endpointId} nil]
       [nil {:message (str "Endpoint with id " endpointId " does not exist") :status 404}]))))

(defn delete-endpoints-by-userId [config userId]
  ;; Using `do` here because this action only has a "success" case
  ;; Don't worry if no endpoints are there; the goal of cleaning up was achieved nonetheless
  (wrap-mongo-exception
   (do
     (mc/remove
      (mg/get-db (:conn config) (:db config)) "endpoints"
      {:userId (ObjectId. userId)})
     [{:userId userId} nil])))

(defn update-endpoint-by-id [config userId endpointId data]
  ;; TODO: Ignore :data field on data and merge with actual data of endpoint
  (wrap-mongo-exception
   (let [result
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:_id (ObjectId. endpointId)
           :userId (ObjectId. userId)}
          data)]
     (if (res/acknowledged? result)
       [{:_id endpointId} nil]
       [nil {:message (str "Endpoint with id " endpointId " does not exist" :status 404)}]))))
