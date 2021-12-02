(ns monmon.logic
  (:require [monmon.persistence :as p])
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
  [endpoint data]
  (or (nil? endpoint) (not (= (keys (first (:data endpoint))) (keys data)))))

(defn on-get [user endpoint]
  (if-let [result (p/get-endpoint user endpoint)]
    (:data result)))

(defn on-get-id [id data]
  "not yet supported")

(defn on-add [user endpoint data]
  ;; TODO: differentiate in future because of versions!
  (let [old (p/get-endpoint user endpoint)]
    (cond
      (nil? old)
      (do (p/add-endpoint user endpoint data)
          "Successfulyl added endpoint")
      (endpoint-changed? old data)
      (do (p/add-version user endpoint data)
          "Successfully added version")
      :else (do
              (p/add-data user endpoint data)
              "Successfully added data"))))

(defn on-put [user endpoint data]
  "not yet supported")

(defn on-delete [user endpoint data]
  "not yet supported")
