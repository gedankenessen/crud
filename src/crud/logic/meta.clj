(ns crud.logic.meta
  (:require [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]))

(defn delete-endpoint [db id]
  [{:message "Successfully deleted endpoint"} nil])

(defn update-endpoint [db id data]
  [{:message "Successfully updated endpoint"} nil])

(defn get-endpoint [db id]
  [{} nil])
