(ns crud.persistence.atom.crud
  (:require [crud.persistence.atom.utility :refer [fresh-timestamp! fresh-uuid!]]))

(defn get-data [atom user endpoint]
  (if-let [result (get-in @atom [:endpoints (keyword user) (keyword endpoint) :data])]
    [(map (fn [[k v]] (assoc v :_id k)) result) nil]
    [nil {:message (str "Could not find endpoint /" endpoint) :status 404}]))

(defn get-data-by-id [atom user endpoint id]
  (if-let [result (get-in @atom [:endpoints (keyword user) (keyword endpoint) :data id])]
    [(assoc result :_id id) nil]
    [nil {:message (str "Could not find item on /" endpoint " with id " id) :status 404}]))

(defn get-data-last [atom user endpoint]
  (if-let
      [result
       (first
        (map
         (fn [[k v]] (assoc v :_id k))
         (get-in @atom [:endpoints (keyword user) (keyword endpoint) :data])))]
    [result nil]
    [nil {:message (str "Could not find endpoint /" endpoint ".") :status 404}]))

(defn add-endpoint [atom userId endpoint new-data]
  (let [endpoint-id (fresh-uuid!)
        data-id (fresh-uuid!)]
    (swap!
     atom
     assoc-in
     [:endpoints userId (keyword endpoint)]
     {:data (assoc {} data-id new-data)
      :_id endpoint-id
      :name endpoint
      :methods []
      :userId userId
      :timestamp (fresh-timestamp!)})
    [{:endpoint-id endpoint-id :data-id data-id} nil]))

(defn add-data [atom user endpoint new-data]
  (if (get-in @atom [:endpoints (keyword user) (keyword endpoint)])
      (let [id (fresh-uuid!)]
        (swap! atom assoc-in [:endpoints (keyword user) (keyword endpoint) :data id] new-data)
        [{:data-id id} nil])
      [nil {:message (str "Endpoint /" endpoint " does not exist") :status 404}]))

(defn add-version [atom user endpoint new-data]
  (if (get-in @atom [:endpoints (keyword user) (keyword endpoint)])
    (let [id (fresh-uuid!)]
      (swap! atom assoc-in [:endpoints (keyword user) (keyword endpoint) :data] {id new-data})
      [{:data-id id} nil])
    [nil {:message (str "Endpoint /" endpoint " does not exist") :status 404}]))

(defn delete-data-by-id [atom user endpoint id]
  (if (get-in @atom [:endpoints (keyword user) (keyword endpoint) :data id])
    (do
      (swap!
       atom
       update-in
       [:endpoints (keyword user) (keyword endpoint) :data]
       dissoc
       id)
      [{:id id} nil])
    [nil {:message (str "Item with id " id " does not exist") :status 404}]))

(defn update-data-by-id [atom user endpoint id new-data]
  (if (get-in @atom [:endpoints (keyword user) (keyword endpoint) :data id])
    (do
      (swap!
       atom
       assoc-in
       [:endpoints (keyword user) (keyword endpoint) :data id]
       new-data)
      [{:id id} nil])
    [nil {:message (str "Item with id " id " does not exist") :status 404}]))
