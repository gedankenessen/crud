(ns crud.logic.core
  (:require
   [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]
   [crud.logic.change :refer [has-changed?]]))

(defn on-get [db userId endpoint]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/get-data db userId endpoint))

(defn on-get-id [db userId endpoint id]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/get-data-by-id db userId endpoint id))

(defn on-post [db userId endpoint new-data]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (-> (persistence/get-data-last db userId endpoint)
      (#(first %))
      (#(dissoc % :id))
      (#(cond
          (nil? %)
          (persistence/add-endpoint db userId endpoint new-data)
          (has-changed? % new-data)
          (persistence/add-version db userId endpoint new-data)
          :else (persistence/add-data db userId endpoint new-data)))))

(defn on-delete-by-id [db userId endpoint id]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/delete-data-by-id db userId endpoint id))

(defn on-put [db userId endpoint id data]
  {:pre [(is-persistence? db)]
   :post [(is-response? %)]}
  (persistence/update-data-by-id db userId endpoint id data))
