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

(defn get-data [user endpoint]
  (if-let [result (p/get-endpoint user endpoint)]
    (:data result)))

(defn endpoint-changed? [user endpoint data]
  (let [e (p/get-endpoint user endpoint)]
    (or (nil? e) (not (= (keys (first (:data e))) (keys data))))))

(defn endpoint-changed? [user endpoint data]
  ;; get the endpoint
  ;; get last entry in "data"
  ;; compare old data with new data
  ;; fields change?
  (let [e (p/get-endpoint user endpoint)]
    (if e
      ;; TODO: compare types (not just keys)
      (not (= (keys (first (:data e))) (keys data)))
      "Endpoint could not be found")))

(comment
  (let [e {:data [ {:name "" :hair ""} ] }
      data {:name "" :hair ""}]
    (not (= (keys (first (:data e))) (keys data)))))

(defn add-endpoint [user endpoint data]
  ;; TODO: differentiate in future because of versions!
  ;; TODO: Use id from endpoint from now on?
  (if (endpoint-changed? user endpoint data)
    (p/update-endpoint user endpoint data)
    (p/add-to-endpoint user endpoint data)))

(defn update-endpoint [user endpoint data]
  (p/update-endpoint user endpoint data)
