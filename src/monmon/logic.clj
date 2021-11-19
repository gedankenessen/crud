(ns monmon.logic
  (:require [monmon.persistence :as p]))

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
  (let [result (p/get-endpoint user endpoint)]
    (if result
      (:data result)
      {})))

(defn add-endpoint [user endpoint data]
  ;; TODO: differentiate in future because of versions!
  (update-endpoint user endpoint data))

(defn endpoint-changed? [user endpoint data])

(defn update-endpoint [user endpoint data]
  (p/update-endpoint user endpoint data))

