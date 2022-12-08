(ns crud.persistence.mongo.user
  (:require [clojure.set :refer [rename-keys]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress] com.mongodb.MongoException))

(defn get-user-by-email [config email]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if-let [result (first
                   (mc/find-maps
                    (mg/get-db (:conn config) (:db config))
                    "users"
                    {:email email}))]
    [result nil]
    [nil {:message (str "Could not find user with email " email) :status 404}]))

(defn get-user-by-id [config id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if-let
      [result (mc/find-map-by-id
               (mg/get-db (:conn config) (:db config))
               "users"
               (ObjectId. id))]
    [result nil]
    [nil {:message (str "Could not find user with id " id) :status 404}]))

(defn add-user [config data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (let [id (ObjectId.)
        result (mc/insert
                (mg/get-db (:conn config) (:db config))
                "users"
                (assoc data :_id id))]
    (if (res/acknowledged? result)
      [{:_id (str id)} nil]
      [nil {:message "Could not add user" :status 500}])))

(defn update-user [config id data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (let [result
        (mc/update-by-id
         (mg/get-db (:conn config) (:db config))
         "users"
         (ObjectId. id)
         ;; Dissoc :_id to remove ability for shenanigans
         ;; Does however expect upper layer to have sanitized data (somewhat)
         (dissoc data :_id))]
    (if (and
         (res/acknowledged? result)
         (res/updated-existing? result))
      [{:_id (str id)} nil]
      [nil {:message "Could not update user" :status 500}])))

(defn delete-user [config id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (let [result
        (mc/remove-by-id
         (mg/get-db (:conn config) (:db config))
         "users"
         (ObjectId. id))]
    (if (res/acknowledged? result)
      [{:_id (str id)} nil]
      [nil {:message "Could not delete user" :status 500}])))
