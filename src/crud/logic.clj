(ns crud.logic
 (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

;; Which operations does the application have?
;; Get data in endpoint (GET)
;; Add endpoint (POST)
;; Check if endpoint changed
;; Update existing endpoint (POST!)

(def constraints
  {:keyword-pro "pro"
   :keyword-free "free"
   :max-endpoints-free 3
   :max-endpoints-pro Integer/MAX_VALUE})

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

(defn on-get [user endpoint data]
  {:event :get-data
   :user user
   :endpoint endpoint
   :data data})

(defn on-get-id [user endpoint id data]
  (cond
    (nil? data) {:event :get-data-id-doesnt-exist}
    :else {:event :get-data-id
           :data data
           :user user
           :endpoint endpoint
           :id id}))

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

(defn on-put [user endpoint id data result]
  (cond
    (true? result)
    {:event :put-data-successful
     :user user
     :endpoint endpoint
     :id id
     :data data}
    :else {:event :put-data-doesnt-exist}))

(defn on-delete-id [user endpoint id]
  {:event :delete-data
   :user user
   :endpoint endpoint
   :id id})

