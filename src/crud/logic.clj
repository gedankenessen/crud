(ns crud.logic
  (:require [crud.persistence :as p])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

;; Which operations does the application have?
;; Get data in endpoint (GET)
;; Add endpoint (POST)
;; Check if endpoint changed
;; Update existing endpoint (POST!)

;; TODO: Afterwards:
;; Get data in endpoint by id (GET)
;; Update data in endpoint by id (PUT)
;; Delete data in endpoint by id (DELETE)

(defn endpoint-changed?
  [old-data new-data]
  ;; Check if keys have changed
  ;; TODO: Walk nested maps, too!
  (not (= (set (keys old-data)) (set (keys new-data)))))

(comment
  ;; Test `endpoint-changed?`
  (endpoint-changed?
   {:x 1 :y 2 :z 3}
   {:y 3 :x 1 :z 0}))

(defn on-get [user endpoint]
  {:event :get-data
   :user user
   :endpoint endpoint})

(defn on-get-id [user endpoint id]
  {:event :get-data-id
   :user user
   :endpoint endpoint
   :id id})

(defn on-add [user endpoint old-data new-data]
  ;; TODO: differentiate in future because of versions!
  (cond
    (nil? old-data)
    {:event :add-endpoint
     :user user
     :endpoint endpoint
     :data new-data}
    (endpoint-changed? old-data new-data)
    {:event :add-version
     :user user
     :endpoint endpoint
     :data new-data}
    :else
    {:event :add-data
     :user user
     :endpoint endpoint
     :data new-data}))

;; TODO: Should this trigger an endpoint change, too?
(defn on-put [user endpoint id data]
  {:event :put-data
   :user user
   :endpoint endpoint
   :id id
   :data data})

(defn on-delete [user endpoint id]
  {:event :delete-data
   :user user
   :endpoint endpoint
   :id id})

