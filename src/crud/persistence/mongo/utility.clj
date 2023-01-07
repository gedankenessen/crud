(ns crud.persistence.mongo.utility
  (:import com.mongodb.MongoException org.bson.types.ObjectId))

(defmacro wrap-mongo-exception [body]
  (try
    body
    (catch MongoException _
      [nil {:message "Something went wrong" :status 500}])))
