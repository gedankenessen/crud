(ns crud.logic.cred
  (:require [clojure.string :refer [lower-case]]
            [buddy.hashers :as hashers]
            [buddy.core.nonce :as nonce]
            [crud.entrypoint.tokens :refer [sign-token config]]))

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

