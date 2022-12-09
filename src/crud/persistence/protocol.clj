(ns crud.persistence.protocol)

;; TODO: Look into extend-protocol best practises
(defprotocol Persistence
  "API for storage (e.g MongoDB)"
  ;;setup
  (connect [config] "Setup db connection")
  ;; crud functions
  (get-data [config userId endpoint] "Get all `data` in `/:endpoint` from user with `userId`")
  (get-data-by-id [config userId endpoint dataId] "Get `data` with `dataId` in `/:endpoint` from user with `userId`")
  (get-data-last [config userId endpoint] "Gets the last `data` entry in `/:endpoint` from user with `userId`")
  (add-endpoint [config userId endpoint new-data] "Add `/:endpoint` with `new-data` for user with `userId`")
  (add-data [config userId endpoint new-data] "Add `new-data` to endpoint with `/:endpoint` from user with `userId`")
  (add-version [config userId endpoint new-data] "Overrides data with `new-data` in `/:endpoint` from `user` with `userId`")
  (delete-data-by-id [config userId endpoint dataId] "Delete `data` with `dataId` in `/:endpoint` from `user` with `userId`")
  (update-data-by-id [config userId endpoint dataId new-data] "Update data of `/:endpoint` with `new-data` from user with `userId`")
  ;; user functions
  (get-user-by-email [config email] "Get user by `email`")
  (get-user-by-id [config userId] "Get user by `userId`")
  (add-user [config data] "Add user with `data`")
  (update-user [config userId data] "Update user with `userId` with `data`")
  (delete-user [config userId] "Delete user with `userId`")
  ;; meta functions
  (get-endpoints [config userId] "Get endpoints from user with `userId`")
  (get-endpoint-by-id [config userId endpointId] "Get endpoint with `endpointId` from user with `userId`")
  (delete-endpoint-by-id [config userId endpointId] "Delete endpoint with `endpointId` from user with `userId`")
  (delete-endpoints-by-userId [config userId] "Delete all endpoints from user with `userId`")
  (update-endpoint-by-id [config userId endpointId new-data] "Update endpoint with `endpointId` from user with `userId` with `new-data`"))

(defn is-persistence? [db]
  (satisfies? Persistence db))

(defn is-response? [response]
  (and (vector? response)
       (= (count response) 2)))
