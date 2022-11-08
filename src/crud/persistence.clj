(ns crud.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer [$push]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(def conn (delay (mg/connect)))

(defn get-endpoint-by-name [user endpoint]
  (:_id
   (mc/find-one-as-map
    (mg/get-db @conn "crud-testing")
    "endpoints"
    {:userId (ObjectId. user) :name endpoint}
    ["_id"])))

(comment
  ;; Test for `get-endpoint-by-name`
  ;; for endpoint `tables`
  ;; of user `63691793518fa064ce036c0c`
  (get-endpoint-by-name "63691793518fa064ce036c0c" "tables"))

(defn get-data [user endpoint]
  ;; TODO: probably should move `db`, `crud-testing` and `endpoints` into config param
  (let [endpoint-id (get-endpoint-by-name user endpoint)]
    ;; TODO: handle if endpoint-id nil
    (:data
     (mc/find-one-as-map
      (mg/get-db @conn "crud-testing")
      "data"
      {:endpointId endpoint-id} ["data"]))))

(comment
  ;; Test for `get-form-endpoint`
  ;; for endpoint `products`
  ;; of user `63691793518fa064ce036c0c`
  (get-data "63691793518fa064ce036c0c" "tables"))

(defn get-data-by-id [user endpoint id]
  (let [endpoint-id (get-endpoint-by-name user endpoint)]
    (:data
     (mc/find-one-as-map
      (mg/get-db @conn "crud-testing")
      "data"
      {:endpointId endpoint-id
       :_id (ObjectId. id)}
      ["data"]))))

(comment
  ;; Run `get-data-by-id`
  (get-data-by-id "63691793518fa064ce036c0c" "tables" "636a652f2352b0441f6bf41f"))

(defn get-data-last [user endpoint]
  (let [endpoint-id (get-endpoint-by-name user endpoint)]
    (:data
     (mc/find-one-as-map
      (mg/get-db @conn "crud-testing")
      "data"
      {:endpointId endpoint-id}
      ["data"]))))

(comment
  ;; Run `get-from-endpoint-last`
  (get-data-last "63691793518fa064ce036c0c" "tables"))

(defn add-endpoint [user endpoint methods data]
  (-> {:_id (org.bson.types.ObjectId.)
       :userId (ObjectId. user)
       :methods methods
       :timestamp (quot (System/currentTimeMillis) 1000)
       :name endpoint}
      (#(mc/insert-and-return
         (mg/get-db @conn "crud-testing")
         "endpoints"
         %))
      :_id
      (#(mc/insert
         (mg/get-db @conn "crud-testing")
         "data"
         {:endpointId %
          :data data}))))

(comment
  ;; Run `add-endpoint`
  (add-endpoint
   "63691793518fa064ce036c0c"
   "focus"
   [:GET :PUT :POST :DELETE]
   {:x 1 :y 2}))

(defn
  add-version
  "Note: `versions` are unsupported for now!"
  [user endpoint data])

(defn add-data [user endpoint data]
  (let [endpoint-id (get-endpoint-by-name user endpoint)]
    (mc/insert
     (mg/get-db @conn "crud-testing")
     "data"
     {:endpointId endpoint-id
      :data data})))

(comment
  ;; Run `add-data`
  (add-data
   "63691793518fa064ce036c0c"
   "focus"
   {:legs 3 :color "brown" :height "40cm" :width "200cm" :length "80cm"}))

(defn update-data [user endpoint id data]
  (let [endpoint-id (get-endpoint-by-name user endpoint)
        id (ObjectId. id)]
    (mc/update
     (mg/get-db @conn "crud-testing")
     "data"
     {:_id id
      :endpointId endpoint-id}
     {:endpointId endpoint-id
      :_id id
      :data data})))

(comment
  ;; Run `update-data`
  (update-data
   "63691793518fa064ce036c0c"
   "focus"
   "636a75a36a263c5cff4da190"
   {:x (+ 10 (int (Math/floor (* 10 (Math/random)))))
    :y (+ 20 (int (Math/floor (* 10 (Math/random)))))}))

(defn delete-data-by-id [user endpoint id]
  "Not yet implemented")

(defn delete-data-from-endpoint [user endpoint]
  "Not yet implemented")

;; TODO:
;; - [ ] Handling errors
;; - [ ] Restricting access to user
;; - [ ] Use aggregation pipeline instead of fetching 2x
