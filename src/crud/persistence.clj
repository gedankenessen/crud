(ns crud.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer [$push]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(def conn (delay (mg/connect)))

(defn get-data [user endpoint]
  ;; TODO: probably should move `db`, `crud-testing` and `endpoints` into config param
  (:data
   (mc/find-one-as-map
    (mg/get-db @conn "crud-testing")
    "endpoints"
    {:userId (ObjectId. user) :name endpoint} ["data"])))

(comment
  ;; Test for `get-form-endpoint`
  ;; for endpoint `products`
  ;; of user `63691793518fa064ce036c0c`
  (get-data "63691793518fa064ce036c0c" "products"))

(defn get-data-by-id [user endpoint id]
  (first
   (filter
    (fn [x] (= (ObjectId. id) (:_id x)))
    (:data
     (mc/find-one-as-map
      (mg/get-db @conn "crud-testing")
      "endpoints"
      {:userId (ObjectId. user)
       :name endpoint}
      ["data"])))))

(comment
  ;; Run `get-data-by-id`
  (get-data-by-id "63691793518fa064ce036c0c" "products" "63691869a9d4a282cd44ed7f"))

(defn get-from-endpoint-last [user endpoint]
  (first
   (:data
    (mc/find-one-as-map
     (mg/get-db @conn "crud-testing")
     "endpoints"
     {:userId (ObjectId. user)
      :name endpoint}
     ["data"]))))

(defn add-endpoint [user endpoint methods data]
  (mc/insert
   (mg/get-db @conn "crud-testing")
   "endpoints"
   {:userId (ObjectId. user)
    :methods methods
    :timestamp (quot (System/currentTimeMillis) 1000)
    :name endpoint
    :data [(assoc data :_id (org.bson.types.ObjectId.))]}))

(comment
  ;; Run `add-endpoint`
  (add-endpoint
   "63691793518fa064ce036c0c"
   "tables"
   [:GET :PUT :POST :DELETE]
   {:legs 4 :color "blue" :height "120cm" :width "120cm" :length "120cm"}))

(defn
  "Note: `versions` are unsupported for now!"
  add-version
  [user endpoint data])

(defn add-data [user endpoint data]
  (mc/update
   (mg/get-db @conn "crud-testing")
   "endpoints"
   {:userId (ObjectId. user) :name endpoint}
   {$push {:data (assoc data :_id (org.bson.types.ObjectId.))}}))

(comment
  ;; Run `add-data`
  (add-data
   "63691793518fa064ce036c0c"
   "tables"
   {:legs 3 :color "brown" :height "40cm" :width "200cm" :length "80cm"}))

(defn update-data [user endpoint id data]
  (mc/update
   (mg/get-db @conn "crud")
   "endpoints"
   {:user u :name endpoint}))

(defn delete-data [user endpoint id]
  "Not yet implemented")

