(ns crud.persistence.atom.core
  (:require [crud.persistence.protocol :refer [Persistence]]
            [crud.persistence.atom.crud :refer :all]))

(def storage
  (atom {}))

(comment
  ;; Example structure of `storage`
  (def example
    {:63691793518fa064ce036c0c
     {:focus
      {:data
       {:636a75a36a263c5cff4da190 {:x 1 :y 0}}}}})
  (def user "63691793518fa064ce036c0c")
  (def endpoint "focus"))

(defrecord Atom-Driver [atom]
  Persistence
  (get-data [db user endpoint] (get-data db user endpoint))
  (get-data-last [db user endpoint] (get-data-last db user endpoint))
  (get-data-by-id [db user endpoint id] (get-data-by-id db user endpoint id))
  (add-endpoint [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (add-data [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (add-version [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (delete-data-by-id [db user endpoint id] (delete-data-by-id db user endpoint id))
  (update-data-by-id [db user endpoint id new-data] (update-data-by-id db user endpoint id new-data)))
