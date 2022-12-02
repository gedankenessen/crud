(ns crud.logic.meta)

(defn delete-endpoint [id]
  [{:message "Successfully deleted endpoint"} nil])

(defn update-endpoint [id data]
  [{:message "Successfully updated endpoint"} nil])

(defn get-endpoint [id]
  [{} nil])
