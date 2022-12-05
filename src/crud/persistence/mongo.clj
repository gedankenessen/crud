(ns crud.persistence.mongo
  (:require [crud.persistence.protocol :refer [Persistence is-persistence? is-response?]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress] com.mongodb.MongoException))

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
  (if-let [response
           (first
            (map
             (fn [[k v]] (assoc v :id (name k)))
             (:data
              (mc/find-one-as-map
               (mg/get-db (:conn config) (:db config))
               "endpoints"
               {:name endpoint
                :userId (ObjectId. user)}
               [(str "data." id)]))))]
    [response nil]
    [nil {:message (str "Item with id " id " on endpoint /" endpoint " does not exist") :status 404}]))

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

(defn update-data-by-id [config user endpoint id new-data]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if (res/acknowledged?
       (mc/update
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. user)
         :name endpoint}
        {$set {(str "data." id) new-data}}))
    [{:id id} nil]
    [nil {:message (str "Could not update item with id " id) :status 500}]))


(defn get-user-by-email [config email]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if-let [result (first
                   (mc/find-maps
                    (mg/get-db (:conn config) (:db config))
                    "users"
                    {:email email}))]
    [(dissoc (assoc result :id (str (:_id result))) :_id) nil]
    [nil {:message (str "Could not find user with email " email) :status 404}]))

(defn get-user-by-id [config id]
  {:pre [(is-persistence? config)]
   :post [(is-response? %)]}
  (if-let
      [result (mc/find-map-by-id
               (mg/get-db (:conn config) (:db config))
               "users"
               (ObjectId. id))]
    [(dissoc (assoc result :id (str (:_id result))) :_id) nil]
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
      [{:id (str id)} nil]
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
      [{:id id} nil]
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
      [{:id id} nil]
      [nil {:message "Could not delete user" :status 500}])))

(defrecord Mongo-Driver [config]
  Persistence
  ;; CRUD functions
  (get-data [config user endpoint] (get-data config user endpoint))
  (get-data-by-id [config user endpoint id] (get-data-by-id config user endpoint id))
  (get-data-last [config user endpoint] (get-data-last config user endpoint))
  (add-endpoint [config user endpoint data] (add-endpoint config user endpoint data))
  (add-data [config user endpoint new-data] (add-data config user endpoint new-data))
  (add-version [config user endpoint new-data] (add-version config user endpoint new-data))
  (delete-data-by-id [config user endpoint id] (delete-data-by-id config user endpoint id))
  (update-data-by-id [config user endpoint id new-data] (update-data-by-id config user endpoint id new-data))
  ;; User related functions
  (get-user-by-email [config email] (get-user-by-email config email))
  (get-user-by-id [config id] (get-user-by-id config id))
  (add-user [config data] (add-user config data))
  (update-user [config id data] (update-user config id data))
  (delete-user [config id] (delete-user config id)))

(def conn (delay (mg/connect)))
(def config {:conn @conn :db "crud-testing"})
(def db (map->Mongo-Driver config))
