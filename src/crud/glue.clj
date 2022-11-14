(ns crud.glue
  (:require [crud.persistence :as p]
            [crud.logic :as logic])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

;; Glues `logic` together with `persistence` to allow consumption for `entrypoints`
;; TODO: Add error handling (e.g trying to delete id that does not exist)
;; TODO: Add meta methods: delete-endpoint, get-version etc.
;;       Probably smarter to move them to seperate namespaces
;;       e.g crud.user.glue -> working with user data
;;           crud.meta.glue -> working with meta endpoints
;; TODO: Add business logic, e.g:
;;       Endpoint names have to be unique
;;       User cannot create infinite endpoints

(defn on-get [user endpoint]
  (p/get-data user endpoint p/config))

(defn on-get-id [user endpoint id]
  (p/get-data-by-id user endpoint id  p/config))

(defn on-add [user endpoint new-data]
  (let [old-data (p/get-data-last user endpoint p/config)
        action (logic/on-add user endpoint old-data new-data)
        f (case (:event action)
            :add-endpoint p/add-endpoint
            :add-data p/add-data
            :add-version p/add-version
            :else nil)]
    (when f
      (f user endpoint new-data p/config))))

(defn on-put [user endpoint id data]
  (p/update-data user endpoint id data p/config))

(defn on-delete [user endpoint]
  (p/delete-endpoint user endpoint))

(defn on-delete-by-id [user endpoint id]
  (p/delete-data-by-id user endpoint id p/config))
