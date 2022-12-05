(ns crud.logic.user
  (:require [clojure.string :refer [lower-case]]
            [buddy.hashers :as hashers]
            [buddy.core.nonce :as nonce]
            [crud.entrypoint.tokens :refer [sign-token config]]
            [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]
            [crud.entrypoint.tokens :refer [sign-token]]))

(def password-config
  {:secret "secret"
   :min-length 8
   :max-length 32
   :alg :bcrypt+blake2b-512})

(defn encrypt-password
  ([password salt]
   (encrypt-password password salt config))
  ([password salt config]
   (hashers/derive password (assoc config :salt salt))))

(defn check-password
  ([raw encrypted salt]
   (check-password raw encrypted salt config))
  ([raw encrypted salt config]
   (hashers/check raw encrypted (assoc config :salt salt))))

(defn check-login
  ([given encrypted salt]
   (check-login given encrypted salt [true nil]))
  ([given encrypted salt response]
   (try
     (cond
       (not given) [nil {:message "Password is missing" :status 400}]
       (empty? given) [nil {:message "Password is an empty string" :status 400}]
       (or (not encrypted)
           (empty? encrypted)) [nil {:message "Something went wrong" :status 500}]
       (check-password given encrypted salt) response
       :else [nil {:message "Email or password are not correct" :status 400}])
     ;; Catch hashing exceptions
     (catch Exception _
       [nil {:message "Something went wrong" :status 500}]))))

(defn register [db {email :email password :password name :name}]
  (let [[_ error] (persistence/get-user-by-email db email)
        salt (nonce/random-bytes 16)]
    (cond
      (not error) [nil {:message (str "Email " email " is already in use") :status 400}]
      (or (nil? email) (empty? email)) [nil {:message "Email is missing" :status 400}]
      (or (nil? name) (empty? name)) [nil {:message "Name is missing" :status 400}]
      (or (nil? password) (empty? password)) [nil {:message "Password is missing" :status 400}]
      ;; TODO: Check password strength
      :else (persistence/add-user db {:email (lower-case email) :password (encrypt-password password salt) :salt (str salt) :name name :membership :free :status :unconfirmed}))))

(defn login [db {email :email password :password}]
  (cond
    (not email) [nil {:message "Email is missing" :status 400}]
    (empty email) [nil {:message "Email is an empty string" :status 400}]
    :else (let [[{encrypted :password salt :salt id :id} error] (persistence/get-user-by-email db (lower-case email))]
            (if error
              [nil error]
              (check-login password encrypted salt (sign-token id))))))

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



