(ns crud.config
  (:require [crud.entrypoint.tokens :as tokens]
            [crud.entrypoint.config :as api]
            [crud.persistence.mongo.config :as mongo]
            [crud.persistence.atom.config :as local]))

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
   :db (case (:db-type args)
         :mongo (mongo/get-config args)
         (local/get-config args))
   :token (tokens/get-config args)})

(def cli-options
  (concat
   mongo/get-cli-options
   api/get-cli-options
   tokens/get-cli-options
   [["-?" "--help"]]))
