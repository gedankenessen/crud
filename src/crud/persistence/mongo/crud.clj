(ns crud.persistence.mongo.crud
  (:require [clojure.set :refer [rename-keys]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?]]
            [crud.persistence.mongo.utility :refer [wrap-mongo-exception]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress] com.mongodb.MongoException))

(defn get-data [config user endpoint]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   [(map
     (fn [[k v]] (assoc v :_id (keyword k)))
     (:data
      (mc/find-one-as-map
       (mg/get-db (:conn config) (:db config))
       "endpoints"
       {:userId (ObjectId. (name user))
        :name endpoint}
       ["data"])))
    nil]))

(defn get-data-by-id [config user endpoint id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   (if-let
       [response
        (first
         (map
          (fn [[k v]] (assoc v :_id (name k)))
          (:data
           (mc/find-one-as-map
            (mg/get-db (:conn config) (:db config))
            "endpoints"
            {:name (name endpoint)
             :userId (ObjectId. (name user))}
            [(str "data." id)]))))]
     [response nil]
     [nil {:message (str "Item with id " id " on endpoint /" endpoint " does not exist") :status 404}])))

(defn get-data-last [config user endpoint]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   [(first
     (map
      (fn [[_ v]] v)
      (:data
       (mc/find-one-as-map
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:name (name endpoint)
         :userId (ObjectId. (name user))}))))
    nil]))

(defn add-endpoint [config user endpoint data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   [(update
     (select-keys
      (mc/insert-and-return
       (mg/get-db (:conn config) (:db config))
       "endpoints"
       {:userId (ObjectId. (name user))
        :name (name endpoint)
        :timestamp (quot (System/currentTimeMillis) 1000)
        :methods []
        :data (assoc {} (str (ObjectId.)) data)})
      [:_id :name])
     :_id
     #(when % (str %)))
    nil]))

(defn add-data [config user endpoint new-data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   (let [id (str (ObjectId.))]
     (if (res/acknowledged?
          (mc/update
           (mg/get-db (:conn config) (:db config))
           "endpoints"
           {:userId (ObjectId. (name user))
            :name (name endpoint)}
           {$set {(str "data." id) new-data}}))
       [{:_id id} nil]
       [nil {:message (str "Endpoint /" endpoint " does not exist") :status 404}]))))

(defn add-version [config user endpoint new-data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   (let [id (str (ObjectId.))]
     (if (res/acknowledged?
          (mc/update
           (mg/get-db (:conn config) (:db config))
           "endpoints"
           {:userId (ObjectId. (name user))
            :name (name endpoint)}
           {$set {:data (assoc {} id new-data)}}))
       [{:_id id} nil]
       [nil {:message (str "Endpoint /" endpoint " does not exist") :status 404}]))))

(defn delete-data-by-id [config user endpoint id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (wrap-mongo-exception
   (let [result
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. (name user))
           :name (name endpoint)}
          {$unset {(str "data." id) 1}})]
     (if (and (res/acknowledged? result)
              (res/updated-existing? result)
              (< 0 (res/affected-count result)))
       [{:_id id} nil]
       [nil {:message (str "Item with id " id " does not exist") :status 404}]))))

(defn update-data-by-id [config user endpoint id new-data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if (res/acknowledged?
       (mc/update
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. (name user))
         :name (name endpoint)}
        {$set {(str "data." id) new-data}}))
    [{:_id id} nil]
    [nil {:message (str "Item with id " id " does not exist") :status 404}]))

