(ns crud.logic.user
  (:require [crud.entrypoint.tokens :refer [sign-token config]]
            [crud.persistence.mongo :as db]))

(defn join [data]
  [{:message "Welcome!"} nil])

(defn login [{email :email password :password}]
  [{:message "Hello!"} nil])

(defn delete [id {email :email password :password}]
  [{:message "Goodbye!"} nil])

(defn details [id]
  (db/get-user db/db id))

(defn change [id data]
  [{} nil])

