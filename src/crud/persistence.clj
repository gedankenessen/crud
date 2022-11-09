(ns crud.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :as res]
            [monger.operators :refer [$set $unset]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(def conn (delay (mg/connect)))

(def config {:conn @conn :db "crud-testing"})

(defn get-endpoint-id-by-name [user endpoint config]
  (:_id
   (mc/find-one-as-map
    (mg/get-db (:conn config) (:db config))
    "endpoints"
    {:userId (ObjectId. user) :name endpoint}
    ["_id"])))

(comment
  ;; Test for `get-endpoint-by-name`
  ;; for endpoint `tables`
  ;; of user `63691793518fa064ce036c0c`
  (get-endpoint-id-by-name "63691793518fa064ce036c0c" "focus" config))

(defn get-data [user endpoint config]
  (vals
   (:data
    (mc/find-one-as-map
     (mg/get-db (:conn config) (:db config))
     "endpoints"
     {:userId (ObjectId. user)
      :name endpoint}
     ["data"]))))

(comment
  ;; Test for `get-form-endpoint`
  ;; for endpoint `products`
  ;; of user `63691793518fa064ce036c0c`
  (get-data "63691793518fa064ce036c0c" "focus" config))

(defn get-data-by-id [user endpoint id config]
  (first
   (vals
    (:data
     (mc/find-one-as-map
      (mg/get-db (:conn config) (:db config))
      "endpoints"
      {:name endpoint
       :userId (ObjectId. user)}
      [(str "data." id)])))))

(comment
  ;; Run `get-data-by-id`
  (get-data-by-id
   "63691793518fa064ce036c0c"
   "focus"
   "636a75a36a263c5cff4da190"
   config))

(defn get-data-last [user endpoint config]
  (first
   (vals
    (:data
     (mc/find-one-as-map
      (mg/get-db (:conn config) (:db config))
      "endpoints"
      {:userId (ObjectId. user)
       :name endpoint}
      ["data"])))))

(comment
  ;; Run `get-from-endpoint-last`
  (get-data-last
   "63691793518fa064ce036c0c"
   "focus"
   config))

(defn add-endpoint
  ([user endpoint data config]
   (add-endpoint user endpoint [:GET :GET-ID :PUT :POST :DELETE] data config))
  ([user endpoint methods data config]
   (update
    (select-keys
     (mc/insert-and-return
      (mg/get-db (:conn config) (:db config))
      "endpoints"
      {:userId (ObjectId. user)
       :name endpoint
       :timestamp (quot (System/currentTimeMillis) 1000)
       :methods methods
       :data (assoc {} (str (ObjectId.)) data)})
     [:_id :name])
    :_id
    #(when % (str %)))))

(comment
  ;; Run `add-endpoint`
  (add-endpoint
   "63691793518fa064ce036c0c"
   "lol"
   [:GET :PUT :POST :DELETE]
   {:x 1 :y 2}
   config))

(defn add-version
  "Resets data field"
  [user endpoint data config]
  (let [dataId (str (ObjectId.))]
    (when
        (res/acknowledged?
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. user)
           :name endpoint}
          {$set {:data (assoc {} dataId data)}}))
      dataId)))

(comment
  ;; Run `add-version`
  (add-version
   "63691793518fa064ce036c0c"
   "focus"
   {:legs 3 :color "brown" :height "40cm" :width "200cm" :length "80cm"}
   config))

(defn add-data [user endpoint data config]
  (let [dataId (str (ObjectId.))]
    (when
        (res/acknowledged?
         (mc/update
          (mg/get-db (:conn config) (:db config))
          "endpoints"
          {:userId (ObjectId. user)
           :name endpoint}
          {$set {(str "data." dataId) data}}))
      dataId)))

(comment
  ;; Run `add-data`
  (add-data
   "63691793518fa064ce036c0c"
   "focus"
   {:legs 3 :color "brown" :height "40cm" :width "200cm" :length "80cm"}
   config))

(defn update-data [user endpoint id data config]
  (when
      (res/acknowledged?
       (mc/update
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. user)
         :name endpoint}
        {$set {(str "data." id) data}}))
    true))

(comment
  ;; Run `update-data`
  (update-data
   "63691793518fa064ce036c0c"
   "focus"
   "636a75a36a263c5cff4da190"
   {:x (+ 10 (int (Math/floor (* 10 (Math/random)))))
    :y (+ 20 (int (Math/floor (* 10 (Math/random)))))}
   config))

(defn delete-data-by-id [user endpoint id config]
  (when
      (res/acknowledged?
       (mc/update
        (mg/get-db (:conn config) (:db config))
        "endpoints"
        {:userId (ObjectId. user)
         :name endpoint}
        {$unset (str "data." id)})))
  true)

(defn delete-data-from-endpoint [user endpoint]
  "Not yet implemented")

;; TODO:
;; - [ ] Handling errors
;; - [ ] Restricting access to user
;; - [x] Use aggregation pipeline instead of fetching 2x
