(ns crud.persistence.protocol)

(defprotocol Persistence
  "API for storage (e.g MongoDB)"
  (get-data [db user endpoint] "Get all `data` in `endpoint` from `user`")
  (get-data-by-id [db user endpoint id] "Get `data` with `id` in `endpoint` from `user`")
  (get-data-last [db user endpoint] "Gets the last `data` entry in `endpoint` from `user`")
  (add-endpoint [db user endpoint new-data] "Add `endpoint` with `new-data` for `user`")
  (add-data [db user endpoint new-data] "Add `data` entry in `endpoint` from `user` with contens of `new-data`")
  (add-version [db user endpoint new-data] "Overrides `data` entry in `endpoint` from `user` with `new-data`")
  (delete-data-by-id [db user endpoint id] "Delete `data` with `id` in `endpoint` from `user` with")
  (update-data-by-id [db user endpoint id new-data] "Update `new-data` with `id` in `endpoint` from `user`"))

(defn is-persistence? [db]
  (satisfies? Persistence db))

(defn is-response? [response]
  (and (vector? response)
       (= (count response) 2)))
