(ns crud.logic.user
  (:require [clojure.string :refer [lower-case]]
            [buddy.hashers :as hashers]
            [buddy.core.nonce :as nonce]
            [crud.entrypoint.tokens :refer [sign-token config]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]
            [crud.logic.cred :refer :all]))

(defn register [db {email :email password :password name :name}]
  (let [[_ error] (persistence/get-user-by-email db email)
        salt (nonce/random-bytes 16)]
    (cond
      (not error) [nil {:message (str "Email " email " is already in use") :status 400}]
      (or (nil? email) (empty? email)) [nil {:message "Email is missing" :status 400}]
      (or (nil? name) (empty? name)) [nil {:message "Name is missing" :status 400}]
      (or (nil? password) (empty? password)) [nil {:message "Password is missing" :status 400}]
      ;; TODO: Check password strength
      :else
      (persistence/add-user
       db
       {:email (lower-case email)
        :password (encrypt-password password salt)
        :salt (str salt)
        :name name
        :membership :free
        :status :unconfirmed}))))

(defn login [db {email :email password :password}]
  (cond
    (not email) [nil {:message "Email is missing" :status 400}]
    (empty email) [nil {:message "Email is an empty string" :status 400}]
    :else (let [[{encrypted :password salt :salt id :_id} error] (persistence/get-user-by-email db (lower-case email))]
            (if error
              [nil error]
              (check-login password encrypted salt (sign-token id))))))

(defn details [db id]
  (let [[result error] (persistence/get-user-by-id db id)]
    (if result
      [(dissoc result :password :email :salt) nil]
      [nil error])))

(defn change [db id data]
  (let [[result error] (persistence/get-user-by-id db id)]
    (if error
      [nil error]
      ;; Currently the only key that is allowed to be changed is `name`
      (persistence/update-user db id (merge result {:name (:name data)})))))

(defn delete [db id {email :email password :password}]
  (cond
    (nil? email) [nil {:message "E-mail is missing in body" :status 400}]
    (empty? email) [nil {:message "E-mail is empty string in body" :status 400}]
    (nil? password) [nil {:message "Password is missing in body" :status 400}]
    (empty? password) [nil {:message "Password is empty string in body" :status 400}]
    :else (let [[{actual-password :password actual-id :_id} error] (persistence/get-user-by-email db email)]
            (cond
              error [nil error]
              (not (= id (str actual-id))) [nil {:message "You can't delete other peoples accounts" :status 400}]
              :else (check-login
                     actual-password
                     password
                     (let [[_ error] (persistence/delete-endpoints-by-userId db id)]
                       (if error
                         error
                         (persistence/delete-user db id))))))))
