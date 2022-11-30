(ns crud.logic.core
  (:require [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]))

(defn endpoint-changed?
  "Check if keys of `data` have changed"
  [old-data new-data]
  ;; TODO: Walk nested maps, too!
  (not (= (set (keys old-data)) (set (keys new-data)))))

(defn on-get [db user endpoint]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/get-data db user endpoint))

(defn on-get-id [db user endpoint id]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/get-data-by-id db user endpoint id))

(defn on-post [db user endpoint new-data]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (-> (persistence/get-data-last db user endpoint)
      (#(first %))
      (#(dissoc % :id))
      (#(cond
          (nil? %)
          (persistence/add-endpoint db user endpoint new-data)
          (endpoint-changed? % new-data)
          (persistence/add-version db user endpoint new-data)
          :else (persistence/add-data db user endpoint new-data)))))

(defn on-delete-by-id [db user endpoint id]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/delete-data-by-id db user endpoint id))

(defn on-put [db user endpoint id data]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/update-data-by-id db user endpoint id data))
