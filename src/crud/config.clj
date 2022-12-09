(ns crud.config
  (:require [crud.entrypoint.tokens :as tokens]
            [crud.entrypoint.config :as api]
            [crud.persistence.mongo.core :as mongo]))

(def config
  {:api api/config
   :db mongo/config
   :tokens tokens/config})
