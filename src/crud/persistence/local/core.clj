(ns crud.persistence.local.core
  (:require [crud.persistence.protocol :refer [Persistence]]
            [crud.persistence.local.crud :refer :all]
            [crud.persistence.local.user :refer :all]
            [crud.persistence.local.meta :refer :all]))

(defrecord Local-Driver [db]
  Persistence
  ;; setup
  (connect [db] db)
  ;; crud functions
  (get-data [db user endpoint] (get-data (:db db) user endpoint))
  (get-data-last [db user endpoint] (get-data-last (:db db) user endpoint))
  (get-data-by-id [db user endpoint id] (get-data-by-id (:db db) user endpoint id))
  (add-endpoint [db user endpoint new-data] (add-endpoint (:db db) user endpoint new-data))
  (add-data [db user endpoint new-data] (add-data (:db db) user endpoint new-data))
  (add-version [db user endpoint new-data] (add-version (:db db) user endpoint new-data))
  (delete-data-by-id [db user endpoint id] (delete-data-by-id (:db db) user endpoint id))
  (update-data-by-id [db user endpoint id new-data] (update-data-by-id (:db db) user endpoint id new-data))
  ;; user functions
  (get-user-by-email [db email] (get-user-by-email (:db db) email))
  (get-user-by-id [db userId] (get-user-by-id (:db db) userId))
  (add-user [db data] (add-user (:db db) data))
  (update-user [db userId data] (update-user (update-user (:db db) userId data)))
  (delete-user [db userId] (delete-user (:db db) userId))
  ;; meta functions
  (get-endpoints [db userId] (get-endpoints (:db db) userId))
  (get-endpoint-by-id [db userId endpointId] (get-endpoint-by-id (:db db) userId endpointId))
  (delete-endpoint-by-id [db userId endpointId] (delete-endpoint-by-id (:db db) userId endpointId))
  (delete-endpoints-by-userId [db userId] (delete-endpoints-by-userId (:db db) userId))
  (update-endpoint-by-id [db userId endpointId data] (update-endpoint-by-id (:db db) userId endpointId data)))
