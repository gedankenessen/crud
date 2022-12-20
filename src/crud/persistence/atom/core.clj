(ns crud.persistence.atom.core
  (:require [crud.persistence.protocol :refer [Persistence]]
            [crud.persistence.atom.crud :refer :all]))

(defrecord Atom-Driver [atom]
  Persistence
  ;; setup
  (connect [config] config)
  ;; crud-funcitons
  (get-data [db user endpoint] (get-data db user endpoint))
  (get-data-last [db user endpoint] (get-data-last db user endpoint))
  (get-data-by-id [db user endpoint id] (get-data-by-id db user endpoint id))
  (add-endpoint [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (add-data [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (add-version [db user endpoint new-data] (add-endpoint db user endpoint new-data))
  (delete-data-by-id [db user endpoint id] (delete-data-by-id db user endpoint id))
  (update-data-by-id [db user endpoint id new-data] (update-data-by-id db user endpoint id new-data))
  ;;
  )
