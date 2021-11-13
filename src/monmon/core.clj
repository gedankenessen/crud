(ns monmon.core
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress])
  (:gen-class))

(defn get-connection []
  (mg/connect))

(comment
  (def connection (get-connection)))

(comment
  (mc/insert (mg/get-db connection "messages") "marlon" {:time (System/currentTimeMillis) :body "hello from clojure"}))

(comment
  (mc/find-maps (mg/get-db connection "messages") "marlon"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


