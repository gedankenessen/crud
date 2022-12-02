(ns crud.logic.user
  (:require [clojure.string :refer [lower-case]]
            [crud.entrypoint.tokens :refer [sign-token config]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]
            [crud.entrypoint.tokens :refer [sign-token]]))

(defn register [db {email :email password :password name :name}]
  (let [[_ error] (persistence/get-user-by-email db email)]
    (cond
      (not error) [nil {:message (str "Email " email " is already in use") :status 400}]
      (or (nil? email) (empty? email)) [nil {:message "Email is missing" :status 400}]
      (or (nil? name) (empty? name)) [nil {:message "Name is missing" :status 400}]
      (or (nil? password) (empty? password)) [nil {:message "Password is missing" :status 400}]
      ;; TODO: Check password strength
      ;; TODO: Salt etc. password (or do so in add-user?)
      :else (persistence/add-user db {:email (lower-case email) :password password :name name :membership :free :status :unconfirmed}))))

(defn login [db {email :email password :password}]
  (let [[{other :password id :id} error] (persistence/get-user-by-email db (lower-case email))]
    (cond
      error [nil error]
      (not email) [nil {:message "Email is missing" :status 400}]
      (not password) [nil {:message "Password is missing" :status 400}]
      (= (lower-case other) (lower-case password)) (sign-token id)
      :else [nil {:message "Email or password are not correct" :status 400}])))

(defn delete [db id {email :email password :password}]
  [{:message "Goodbye!"} nil])

(defn details [db id]
  (persistence/get-user-by-id db id))

(defn change [db id data]
  [{} nil])

