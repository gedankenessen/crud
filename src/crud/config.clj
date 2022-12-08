(ns crud.config
  (:require [crud.entrypoint.tokens :as tokens]
            [crud.entrypoint.config :as api]
            [crud.persistence.mongo.core :as mongo]))

(comment
  ;; Example configuration
  {:api {:port 3004
         :host "127.0.0.1"
         :env :prod}
   :db {:port 27017
        :host "127.0.0.1"
        :db "crud-testing"
        :conn nil}
   :tokens {:secret "testing"
            :version 0}})

(def config
  {:api api/config
   :db mongo/db
   :tokens tokens/config})
