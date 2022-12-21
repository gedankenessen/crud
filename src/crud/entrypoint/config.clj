(ns crud.entrypoint.config
  (:require [crud.utility :refer [remove-nils build-config]]))

(def config
  "Defaults for API arguments"
  {:port 3004
   :host "127.0.0.1"
   :env :prod})

(def relevant-keys
  "Subset of keys from entire config, that are relevant for this the API (e.g port is relevant, db-password isn't)"
  [:port :host :env])


(defn get-env-config
  "Retrieve relevant env-variables and assign to keys"
  []
  {:port (System/getenv "CRUD_PORT")
   :host (System/getenv "CRUD_HOST")
   :env (System/getenv "CRUD_ENV")})

(defn get-args-config
  "Map from cli argument names to config key names"
  [{port :crud-port host :crud-host env :crud-env}]
  {:port port :host host :env env})

(defn get-config
  "Combine defaults, env-variables and cli arguments together"
  [args]
  (build-config
   relevant-keys
   config
   (get-env-config)
   (get-args-config args)))
