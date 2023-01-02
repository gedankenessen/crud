(ns crud.persistence.local.crud
  (:require [crud.persistence.local.utility :refer [fresh-timestamp! fresh-uuid!]]))

(defn get-data [db user endpoint]
  (if-let [result (get-in @db [:endpoints (keyword user) (keyword endpoint) :data])]
    [(map (fn [[k v]] (assoc v :_id k)) result) nil]
    [[] nil]))

(defn get-data-by-id [db user endpoint id]
  (if-let [result (get-in @db [:endpoints (keyword user) (keyword endpoint) :data id])]
    [(assoc result :_id id) nil]
    [nil {:message (str "Could not find item on /" endpoint " with id " id) :status 404}]))

(defn get-data-last [db user endpoint]
  (if-let
      [result
       (first
        (map
         (fn [[k v]] (assoc v :_id k))
         (get-in @db [:endpoints (keyword user) (keyword endpoint) :data])))]
    [result nil]
    [nil nil]))

(defn add-endpoint [db userId endpoint new-data]
  (let [endpoint-id (fresh-uuid!)
        data-id (fresh-uuid!)]
    (swap!
     db
     assoc-in
     [:endpoints userId (keyword endpoint)]
     {:data (assoc {} data-id new-data)
      :_id endpoint-id
      :name endpoint
      :methods []
      :userId userId
      :timestamp (fresh-timestamp!)})
    [{:endpoint-id endpoint-id :data-id data-id} nil]))

(defn add-data [db user endpoint new-data]
  (if (get-in @db [:endpoints (keyword user) (keyword endpoint)])
    (let [id (fresh-uuid!)]
      (swap! db assoc-in [:endpoints (keyword user) (keyword endpoint) :data id] new-data)
      [{:data-id id} nil])
    [nil {:message (str "Endpoint /" endpoint " does not exist") :status 404}]))

(defn add-version [db user endpoint new-data]
  (if (get-in @db [:endpoints (keyword user) (keyword endpoint)])
    (let [id (fresh-uuid!)]
      (swap! db assoc-in [:endpoints (keyword user) (keyword endpoint) :data] {id new-data})
      [{:data-id id} nil])
    [nil {:message (str "Endpoint /" endpoint " does not exist") :status 404}]))

(defn delete-data-by-id [db user endpoint id]
  (if (get-in @db [:endpoints (keyword user) (keyword endpoint) :data id])
    (do
      (swap!
       db
       update-in
       [:endpoints (keyword user) (keyword endpoint) :data]
       dissoc
       id)
      [{:id id} nil])
    [nil {:message (str "Item with id " id " does not exist") :status 404}]))

(defn update-data-by-id [db user endpoint id new-data]
  (if (get-in @db [:endpoints (keyword user) (keyword endpoint) :data id])
    (do
      (swap!
       db
       assoc-in
       [:endpoints (keyword user) (keyword endpoint) :data id]
       new-data)
      [{:id id} nil])
    [nil {:message (str "Item with id " id " does not exist") :status 404}]))
