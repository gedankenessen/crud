(ns crud.persistence.local.meta)

(defn get-endpoints [db userId]
  [(map
    (fn [[k v]] (assoc v :name k))
    (get-in @db [:endpoints userId]))
   nil])

(defn get-endpoint-by-id [db userId endpointId]
  (if-let
      [result
       (first
        (map
         (fn [[k v]] (assoc v :name k))
         (filter
          (fn [[_ {id :_id}]] (= endpointId id))
          (get-in @db [:endpoints userId]))))]
    [result nil]
    [nil {:message (str "Endpoint with id " endpointId " does not exist") :status 404}]))

(defn delete-endpoint-by-id [db userId endpointId]
  (if-let
      [[k v]
       (first
        (filter
         (fn [[_ {id :_id}]] (= endpointId id))
         (get-in @db [:endpoints userId])))]
    (do
      (swap! db update-in [:endpoints userId] dissoc k)
      [{:endpointId endpointId} nil])
    [nil {:message (str "Endpoint with id " endpointId " does not exist") :status 404}]))

(defn delete-endpoints-by-userId [db userId]
  (do
    (swap! db update-in [:endpoints userId] {})
    [{:userId userId} nil]))

(defn update-endpoint-by-id [db userId endpointId data]
  (if-let [[k v]
           (first (filter
                   (fn [[_ v]] (= endpointId (:_id v)))
                   (get-in @db [:endpoints userId])))]
    (do
      (swap! db assoc-in [:endpoints userId k] data)
      [{:endpointId v} nil])
    [nil {:message (str "Endpoint with id " endpointId " does not exist") :status 404}]))
