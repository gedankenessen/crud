(ns crud.config
  (:require [crud.entrypoint.tokens :as tokens]
            [crud.entrypoint.config :as api]
            [crud.persistence.mongo.config :as mongo]))

(def config
  "Returns default config"
  {:api api/config
   :db mongo/config
   :token tokens/config})

(defn get-config
  "Returns config with defaults overriden by command line args and enviroment variables.
  Priority is: args > env > defaults."
  [args]
  {:api (api/get-config args)
   :db (mongo/get-config args)
   :token (tokens/get-config args)})
