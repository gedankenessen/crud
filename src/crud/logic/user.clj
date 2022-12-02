(ns crud.logic.user
  (:require [clojure.string :refer [lower-case]]
            [crud.entrypoint.tokens :refer [sign-token config]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]
            [crud.entrypoint.tokens :refer [sign-token]]))

(defn check-login
  ([actual-password given-password]
   (check-login actual-password given-password [true nil]))
  ([actual-password given-password response]
   (cond
     (not given-password) [nil {:message "Password is missing" :status 400}]
     (empty? given-password) [nil {:message "Password is an empty string" :status 400}]
     (or (not actual-password)
         (empty? actual-password)) [nil {:message "Something went wrong" :status 500}]
     (= (lower-case actual-password) (lower-case given-password)) response
     :else [nil {:message "Email or password are not correct" :status 400}])))

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
  (cond
    (not email) [nil {:message "Email is missing" :status 400}]
    (empty email) [nil {:message "Email is an empty string" :status 400}]
    :else (let [[{other :password id :id} error] (persistence/get-user-by-email db (lower-case email))]
            (if error
              [nil error]
              (check-login other password (sign-token id))))))

(defn details [db id]
  (let [[result error] (persistence/get-user-by-id db id)]
    (if result
      [(dissoc (dissoc result :password) :email) nil]
      [nil error])))

(defn change [db id data]
  (let [[result error] (persistence/get-user-by-id db id)]
    (if error
      [nil error]
      ;; Currently the only key that is allowed to be changed is `name`
      (persistence/update-user db id (merge result {:name (:name data)})))))

(defn delete [db id {email :email password :password}]
  (let [[{actual-password :password actual-id :id} error] (persistence/get-user-by-email db email)]
    (cond
      error [nil error]
      (not (= id actual-id)) [nil {:message "You can't delete other peoples accounts" :status 400}]
      :else (check-login actual-password password (persistence/delete-user db id)))))



