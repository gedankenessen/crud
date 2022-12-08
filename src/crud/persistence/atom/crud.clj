(ns crud.persistence.atom.crud
 (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(defn get-data [atom user endpoint]
  (if-let [result (get-in @atom [(keyword user) (keyword endpoint) :data])]
    [(map (fn [[k v]] (assoc v :id (name k))) result) nil]
    [nil {:message (str "Could not find endpoint /" endpoint) :status 404}]))

(defn get-data-by-id [atom user endpoint id]
  (if-let [result (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])]
    [(assoc result :id (name id)) nil]
    [nil {:message (str "Could not find item on /" endpoint " with id " id) :status 404}]))

(defn get-data-last [atom user endpoint]
  (if-let
      [result
       (first
        (map
         (fn [[k v]] (assoc v :id (name k)))
         (get-in @atom [(keyword user) (keyword endpoint) :data])))]
    [result nil]
    [nil {:message (str "Could not find endpoint /" endpoint ".") :status 404}]))

(defn add-endpoint [atom user endpoint new-data]
  (let [id (ObjectId.)]
    (swap!
     atom
     assoc-in
     [(keyword user) (keyword endpoint)]
     {:data (assoc {} (keyword (str id)) new-data)
      :name endpoint
      :methods []
      :userId (ObjectId. user)
      :timestamp (quot (System/currentTimeMillis) 1000)})
    [{:id id} nil]))

(defn add-data [atom user endpoint new-data]
  (let [id (ObjectId.)]
    (swap! atom assoc-in [(keyword user) (keyword endpoint) :data (keyword (str id))] new-data)
    [{:id id} nil]))

(defn add-version [atom user endpoint new-data]
  (let [id (ObjectId.)]
    (swap! atom assoc-in [(keyword user) (keyword endpoint)] {:data (assoc {} (keyword (str id)) new-data)})
    [{:id id} nil]))

(defn delete-data-by-id [atom user endpoint id]
  (do
    (swap!
     atom
     update-in
     [(keyword user) (keyword endpoint) :data]
     dissoc
     (keyword id))
    [{:id id} nil]))

(defn update-data-by-id [atom user endpoint id new-data]
  (do
    (swap!
     atom
     assoc-in
     [(keyword user) (keyword endpoint) :data (keyword id)]
     new-data)
    [{:id id} nil]))
