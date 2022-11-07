(ns crud.glue
  (:require [crud.persistence :as p]
            [crud.logic :as logic])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

;; Glues `logic` together with `persistence` to allow consumption for `entrypoints`
;; TODO: Add error handling (e.g trying to delete id that does not exist)
;; TODO: Add meta methods: delete endpoint, get version etc.
;;       Probably smarter to move them to seperate namespaces
;;       e.g crud.user.glue -> working with user data
;;           crud.meta.glue -> working with meta endpoints


(defn on-get [user endpoint]
  (let [action (logic/on-get user endpoint)
        result (p/get-from-endpoint user endpoint)]
    (if (nil? (:data result))
      {:status 404 :message "Endpoint could not be found"}
      (merge {:status 200} result))))

(defn on-get-id [user endpoint id]
  (let [action (logic/on-get-id user endpoint id)
        result (p/get-from-endpoint-by-id user endpoint id)]
    (if (nil? (:data result))
      {:status 404 :message "Object with id could not be found"}
      (merge {:status 200} result))))

(defn on-add [user endpoint new-data]
  (let [old-data (p/get-from-endpoint-last user endpoint)
        action (logic/on-add user endpoint old-data new-data)]
    (case (:event action)
      ;; TODO: There is probably a more elegant solution for 3x(user endpoint new-data) ... etc.
      :add-endpoint (merge {:status 200 } (p/add-endpoint user endpoint new-data))
      :add-data (merge {:status 200} (p/add-data user endpoint new-data))
      :add-version (merge {:status 200 } (p/add-version user endpoint new-data))
      :else {:status 500})))

(defn on-put [user endpoint id data]
  ;; TODO: No use for action?
  (let [action (logic/on-put user endpoint id data)
        result (p/update-data user endpoint id data)]
    (if (nil? (:data result))
      {:status 404 :message "Object with id could not be found"}
      (merge {:status 200} result))))

(defn on-delete [user endpoint id]
  (p/delete-data user endpoint id))
