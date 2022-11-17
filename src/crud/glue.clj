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
;;       User cannot create infinite endpoints

(defn on-get [user endpoint]
  (response (p/get-data user endpoint p/config)))

(defn on-get-id [user endpoint id]
  (->
   (logic/on-get-id user endpoint id (p/get-data-by-id user endpoint id  p/config))
   (#(case (:event %)
       :get-data-id (response (:data %))
       :get-data-id-doesnt-exist (not-found {:message (str "Could not find item with id " id)})))))

(defn on-add [user endpoint new-data]
  (-> (p/get-data-last user endpoint p/config)
      (#(logic/on-add user endpoint (dissoc % :id) new-data))
      (#(case (:event %)
          ;; TODO: Can probably cut down on duplicate code here
          :add-endpoint (response {:id (:_id (p/add-endpoint (:user %) (:endpoint %) (:data %) p/config))})
          :add-data (response {:id (p/add-data (:user %) (:endpoint %) (:data %) p/config)})
          :add-version (response {:id (p/add-version (:user %) (:endpoint %) (:data %) p/config)})
          :else (status {:body {:message "Something went wrong when adding data"}} 500)))))

(defn on-put [user endpoint id data]
  (-> (p/update-data user endpoint id data p/config)
      (#(logic/on-put user endpoint id data %))
      (#(case (:event %)
          :put-data-successful (response data)
          :put-data-doesnt-exist (status {:body {:message (str "Could not find item with " id)}})
          :else (status {:body {:message "Something went wrong when changing data"}} 500)))))

(defn on-delete-by-id [user endpoint id]
  (p/delete-data-by-id user endpoint id p/config))
