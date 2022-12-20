(ns crud.persistence.atom.core
  (:require [crud.persistence.protocol :refer [Persistence]]
            [crud.persistence.atom.crud :refer :all]
            [crud.persistence.atom.user :refer :all]
            [crud.persistence.atom.meta :refer :all]))

(defrecord Atom-Driver [atom]
  Persistence
  ;; setup
  (connect [initial] (atom initial))
  ;; crud functions
  (get-data [db user endpoint] (get-data db user endpoint))
  (get-data-last [db user endpoint] (get-data-last db user endpoint))
  (get-data-by-id [db user endpoint id] (get-data-by-id db user endpoint id))
  (add-endpoint [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (add-data [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (add-version [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (delete-data-by-id [db user endpoint id] (delete-data-by-id db user endpoint id))
  (update-data-by-id [db user endpoint id new-data] (update-data-by-id db user endpoint id new-data))
  ;; user functions
  (get-user-by-email [db email] (get-user-by-email db email))
  (get-user-by-id [db userId] (get-user-by-id db userId))
  (add-user [db data] (add-user db data))
  (update-user [db userId data] (update-user (update-user db userId data)))
  (delete-user [db userId] (delete-user db userId))
  ;; meta functions
  (get-endpoints [db userId] (get-endpoints db userId))
  (get-endpoint-by-id [db userId endpointId] (get-endpoint-by-id db userId endpointId))
  (delete-endpoint-by-id [db userId endpointId] (delete-endpoint-by-id db userId endpointId))
  (delete-endpoints-by-userId [db userId] (delete-endpoints-by-userId db userId))
  (update-endpoint-by-id [db userId endpointId data] (update-endpoint-by-id db userId endpointId data)))
