(ns crud.persistence.local.user
  (:require [crud.persistence.local.utility :refer [fresh-uuid!]]))

(defn get-user-by-email [db email]
  (if-let
      [result
       (first
        (map
         (fn [[k v]] (assoc v :_id k))
         (filter
          (fn [[k v]] (= email (:email v)))
          (:users @db))))]
    [result nil]
    [nil {:message (str "Could not find user with email " email) :status 404}]))

(defn get-user-by-id [db id]
  (if-let [result (get-in @db [:users id])]
    [(assoc result :_id id) nil]
    [nil {:message (str "Could not find user with id " id) :status 404}]))

(defn add-user [db data]
  (let [user-id (fresh-uuid!)]
    (swap! db assoc-in [:users user-id] data)
    [{:id user-id} nil]))

(defn update-user [db id data]
  (if (get-in @db [:users id])
    (do
      (swap! db assoc-in [:users id] data)
      [{:id id} nil])
    [nil {:message (str "User with id " id " does not exist") :status 404}]))

(defn delete-user [db id]
  (if (get-in @db [:users id])
    (do
      (swap! db update-in [:users] dissoc id)
      [{:id id} nil])
    [nil {:message (str "User with id " id " does not exist") :status 404}]))
