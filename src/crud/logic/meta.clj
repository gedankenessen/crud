(ns crud.logic.meta
  (:require [crud.persistence.protocol :refer [Persistence is-persistence? is-response?] :as persistence]))

(def get-endpoints-by-userId persistence/get-endpoints)

(def get-endpoint-by-id persistence/get-endpoint-by-id)

(def delete-endpoints-by-userId persistence/delete-endpoints-by-userId)

(def delete-endpoint-by-id persistence/delete-endpoint-by-id)

(defn update-endpoint-by-id
  "Update fields on endpoint. Currently only allows changes on :name and :methods."
  [config userId endpointId new-data]
  (persistence/update-endpoint-by-id config userId endpointId (select-keys new-data [:name :methods])))
