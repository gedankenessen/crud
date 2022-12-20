(ns crud.persistence.atom.user
  (:require [crud.persistence.atom.utility :refer [fresh-uuid!]]))

(defn get-user-by-email [atom email]
  (if-let
      [result
       (first
        (map
         (fn [[k v]] (assoc v :_id k))
         (filter
          (fn [[k v]] (= email (:email v)))
          (:users @atom))))]
    [result nil]
    [nil {:message (str "Could not find user with email " email) :status 404}]))

(defn get-user-by-id [atom id]
  (if-let [result (get-in @atom [:users id])]
    [(assoc result :_id id) nil]
    [nil {:message (str "Could not find user with id " id) :status 404}]))

(defn add-user [atom data]
  (let [user-id (fresh-uuid!)]
    (swap! atom assoc-in [:users user-id] data)
    [{:id user-id} nil]))

(defn update-user [atom id data]
  (if (get-in @atom [:users id])
    (do
      (swap! atom assoc-in [:users id] data)
      [{:id id} nil])
    [nil {:message (str "User with id " id " does not exist") :status 404}]))

(defn delete-user [atom id]
  (if (get-in @atom [:users id])
    (do
      (swap! atom update-in [:users] dissoc id)
      [{:id id} nil])
    [nil {:message (str "User with id " id " does not exist") :status 404}]))
