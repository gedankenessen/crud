(ns crud.persistence.mongo
  (:require [crud.persistence.protocol :refer [Persistence]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(defrecord Mongo-Driver [config]
  Persistence
  ;; Implementation of functions
  ;; TODO: Handling errors
  ;; TODO: Return [data error] structure
  ;; TODO: Probably better to have functions in seperate ns (or out of record def)
  (get-data [config user endpoint]
    [(map
      (fn [[k v]] (assoc v :id (name k)))
      (:data
       (mc/find-one-as-map
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. user)
         :name endpoint}
        ["data"])))
     nil])
  (get-data-by-id [config user endpoint id]
    [(first
      (map
       (fn [[k v]] (assoc v :id (name k)))
       (:data
        (mc/find-one-as-map
         (mg/get-db (:conn config) (:db config))
         "endpoints"
         {:name endpoint
          :userId (ObjectId. user)}
         [(str "data." id)])))) nil])
  (get-data-last [config user endpoint]
    [(first
      (map
       (fn [[k v]] (assoc v :id (name k)))
       (:data
        (mc/find-one-as-map
         (mg/get-db (:conn config) (:db config))
         "endpoints"
         {:userId (ObjectId. user)
          :name endpoint}
         ["data"])))) nil])
  (add-endpoint [config user endpoint data]
    [(update
     (select-keys
      (mc/insert-and-return
       (mg/get-db (:conn config) (:db config))
       "endpoints"
       {:userId (ObjectId. user)
        :name endpoint
        :timestamp (quot (System/currentTimeMillis) 1000)
        :methods []
        :data (assoc {} (str (ObjectId.)) data)})
      [:_id :name])
     :_id
     #(when % (str %)))] nil)
  (add-data [config user endpoint new-data]
    (let [dataId (str (ObjectId.))]
      (if (res/acknowledged?
           (mc/update
            (mg/get-db (:conn config) (:db config))
            "endpoints"
            {:userId (ObjectId. user)
             :name endpoint}
            {$set {(str "data." dataId) new-data}}))
        [{:id dataId} nil]
        [nil {:message (str "Could not add endpoint /" endpoint) :status 500}])))
  (add-version [config user endpoint new-data]
    (let [dataId (str (ObjectId.))]
      (if (res/acknowledged?
           (mc/update
            (mg/get-db (:conn config) (:db config))
            "endpoints"
            {:userId (ObjectId. user)
             :name endpoint}
            {$set {:data (assoc {} dataId new-data)}}))
        [{:id dataId} nil]
        [nil {:message (str "Could not add data to endpoint /" endpoint) :status 500}])))
  (delete-data-by-id [config user endpoint id]
    (if (res/acknowledged?
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. user)
           :name endpoint}
          {$unset {(str "data." id) 1}}))
      [{:id id} nil]
      [nil {:message (str "Could not delete item with id " id) :status 500}]))
  (update-data-by-id [config user endpoint id data]
    (if (res/acknowledged?
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. user)
           :name endpoint}
          {$set {(str "data." id) data}}))
      [{:id id} nil]
      [nil {:message (str "Could not update item with id " id) :status 500}])))

(def conn (delay (mg/connect)))
(def config {:conn @conn :db "crud-testing"})
(def db (map->Mongo-Driver config))



