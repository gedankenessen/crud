(ns crud.persistence.atom
  (:require [crud.persistence.protocol :refer [Persistence]])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(def storage
  (atom {:63691793518fa064ce036c0c
         {:focus
          {:data
           {:636a75a36a263c5cff4da190 {:x 1 :y 0}}}}}))

(defrecord Atom-Driver [atom]
  Persistence
  ;; TODO: Handling errors
  ;; TODO: Implement remaining
  (get-data-last [atom user endpoint]
    (if-let [result (first
                     (map
                      (fn [[k v]] (assoc v :id (name k)))
                      (:data (get (get @atom (keyword user)) (keyword endpoint)))))]
      [result nil]
      ;; TODO: Could also be user
      [nil {:message (str "Could not find endpoint /" endpoint ".") :status 404}]))
  (add-data [config user endpoint new-data]
    [nil {:message "Not implemented" :status 501}])
  (add-version [db user endpoint new-data]
    [nil {:message "Not implemented" :status 501}])
  (delete-data-by-id [db user endpoint id]
    [nil {:message "Not implemented" :status 501}])
  (update-data-by-id [db user endpoint id data]
    [nil {:message "Not implemented" :status 501}]))


