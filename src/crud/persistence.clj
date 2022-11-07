(ns crud.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(def conn (delay (mg/connect)))

(defn get-from-endpoint [user endpoint]
  (let [db (mg/get-db @conn "crud")]
    (mc/find-one-as-map db "endpoints" {:user (ObjectId. user) :name endpoint})))

;; TODO: Currently incorrect: Gets "endpoint" by id, instead of an element of `endpoint` by id
(defn get-from-endpoint-by-id [user endpoint id]
  (let [db (mg/get-db @conn "crud")]
    (mc/find-by-id
     db
     "endpoints"
     (ObjectId. id))))

(defn get-an-endpoint-by-id [user id]
  (let [db (mg/get-db @conn "crud")]
    (mc/find-by-id
     db
     "endpoints"
     (ObjectId. id))))

(defn get-from-endpoint-last [user endpoint]
  "Not yet implemented")

(defn add-endpoint [user endpoint data]
  (let [db (mg/get-db @conn "crud")
        u (ObjectId. user)]
    (mc/insert
     db
     "endpoints"
     {:user u :name endpoint :data [data]})))

(defn add-version [user endpoint data]
  (let [db (mg/get-db @conn "crud")
        u (ObjectId. user)]
    (mc/upsert
     db
     "endpoints"
     {:user u :name endpoint}
     {:user u :name endpoint :data [data]}))
  "POST: Successfully added endpoint version")

(defn add-data [user endpoint data]
  (let [db (mg/get-db @conn "crud")
        u (ObjectId. user)]
    (mc/update
     db
     "endpoints"
     {:user u :name endpoint}
     {$push {:data data}}))
  ;; TODO: Error handling+return id
  "POST: Successfully added data")


(defn update-data [user endpoint id data]
  (let [db (mg/get-db @conn "crud")
        u (ObjectId. user)]
    (mc/update
     db
     "endpoints"
     {:user u :name endpoint}
     )))

(defn delete-data [user endpoint id]
  "Not yet implemented")


(comment
  (add-endpoint "619806ebd901bc53b9783241"
                "quadrants"
                {:id 0 :name "Datastores"})
  (add-version "619806ebd901bc53b9783241"
               "quadrants"
               {:id 0 :name "Datastores"})
  (add-data "619806ebd901bc53b9783241"
            "quadrants"
            {:id 3 :name "Languages"}))


(comment
  (defn get-db []
    (persistence/get-connection "crud"))

  (defn test-create-endpoint []
    (persistence/create-endpoint
     (get-db)
     "61967b528c1b2d65e15b9717"
     "items"
     persistence/all-methods
     {:name "brita wasserfilter"
      :width 12.5
      :height 45
      :unit "cm"}))

  (defn test-add-endpoint-version []
    (persistence/add-endpoint-version
     (get-db)
     "6196b76fd008dc356948bed6"
     "items"
     persistence/all-methods
     {:name "ikea table",
      :width 125,
      :height 80,
      :unit "cm"
      :color "bamboo"}))

  )
