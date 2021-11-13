(ns monmon.persistence
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId [com.mongodb MongoOptions ServerAddress]))

(defn get-connection []
  (mg/connect))

(comment
  (def connection (get-connection)))

(comment
  (mc/insert (mg/get-db connection "messages") "marlon" {:time (System/currentTimeMillis) :body "hello from clojure"}))

(comment
  (mc/find-maps (mg/get-db connection "messages") "marlon"))

(comment
  (mc/remove-by-id (mg/get-db connection "messages") "marlon" (mc/ObjectId. "61900a900bdba047b5b0e7cc")))


