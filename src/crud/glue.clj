(ns crud.glue
  (:require [crud.persistence :as p]
            [crud.logic :as logic]
            [ring.util.response :refer [response not-found status]])
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
  (response (p/get-data user endpoint p/config)))

(defn on-get-id [user endpoint id]
  (->
   (logic/on-get-id user endpoint id (p/get-data-by-id user endpoint id  p/config))
   (#(case (:event %)
       :get-data-id (response (:data %))
       :get-data-id-doesnt-exist (not-found (str "Could not find a " endpoint " with id " id))))))

(defn on-add [user endpoint new-data]
  (-> (p/get-data-last user endpoint p/config)
      (#(logic/on-add user endpoint % new-data))
      (#(case (:event %)
          ;; TODO: Can probably cut down on duplicate code here
          :add-endpoint (response (p/add-endpoint (:user %) (:endpoint %) (:data %) p/config))
          :add-data (response (p/add-data (:user %) (:endpoint %) (:data %) p/config))
          :add-version (response (p/add-version (:user %) (:endpoint %) (:data %) p/config))
          :else (status "Something went wrong when adding data" 500)))))

(defn on-put [user endpoint id data]
  (p/update-data user endpoint id data p/config))

(defn on-delete [user endpoint]
  (p/delete-endpoint user endpoint))

(defn on-delete-by-id [user endpoint id]
  (p/delete-data-by-id user endpoint id p/config))


(comment
  (-> {:data 1}
      (#(assoc % :event :xD))
      (#(case (:event %)
          :xD (:data %)
          :else 404))))
