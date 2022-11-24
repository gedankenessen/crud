(ns crud.persistence.mongo
  (:require [crud.persistence.protocol :refer [Persistence is-persistence? is-response?]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

;; TODO: Handle db errors (e.g no connection)

(defn get-data [config user endpoint]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
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

(defn get-data-by-id [config user endpoint id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  [(first
    (map
     (fn [[k v]] (assoc v :id (name k)))
     (:data
      (mc/find-one-as-map
       (mg/get-db (:conn config) (:db config))
       "endpoints"
       {:name endpoint
        :userId (ObjectId. user)}
       [(str "data." id)]))))
   nil])

(defn get-data-last [config user endpoint]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  [(first
    (map
     (fn [[k v]] (assoc v :id (name k)))
     (:data
      (mc/find-one-as-map
       (mg/get-db (:conn config) (:db config))
       "endpoints"
       {:userId (ObjectId. user)
        :name endpoint}
       ["data"]))))
   nil])

(defn add-endpoint [config user endpoint data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
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
    #(when % (str %)))
   nil])

(defn add-data [config user endpoint new-data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (let [id (str (ObjectId.))]
    (if (res/acknowledged?
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. user)
           :name endpoint}
          {$set {(str "data." id) new-data}}))
      [{:id id} nil]
      [nil {:message (str "Could not add endpoint /" endpoint) :status 500}])))

(defn add-version [config user endpoint new-data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (let [id (str (ObjectId.))]
    (if (res/acknowledged?
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. user)
           :name endpoint}
          {$set {:data (assoc {} id new-data)}}))
      [{:id id} nil]
      [nil {:message (str "Could not add data to endpoint /" endpoint) :status 500}])))

(defn delete-data-by-id [config user endpoint id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if (res/acknowledged?
       (mc/update
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. user)
         :name endpoint}
        {$unset {(str "data." id) 1}}))
    [{:id id} nil]
    [nil {:message (str "Could not delete item with id " id) :status 500}]))

(defn update-data-by-id [config user endpoint id data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if (res/acknowledged?
       (mc/update
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. user)
         :name endpoint}
        {$set {(str "data." id) data}}))
    [{:id id} nil]
    [nil {:message (str "Could not update item with id " id) :status 500}]))

(defrecord Mongo-Driver [config]
  Persistence
  ;; Implementation of functions
  (get-data [config user endpoint] (get-data config user endpoint))
  (get-data-by-id [config user endpoint id] (get-data-by-id config user endpoint id))
  (get-data-last [config user endpoint] (get-data-last config user endpoint))
  (add-endpoint [config user endpoint data] (add-endpoint config user endpoint data))
  (add-data [config user endpoint new-data] (add-data config user endpoint new-data))
  (add-version [config user endpoint new-data] (add-version config user endpoint new-data))
  (delete-data-by-id [config user endpoint id] (delete-data-by-id config user endpoint id))
  (update-data-by-id [config user endpoint id data] (update-data-by-id config user endpoint id data)))

(def conn (delay (mg/connect)))
(def config {:conn @conn :db "crud-testing"})
(def db (map->Mongo-Driver config))



