(ns crud.entrypoint.config
  (:require [crud.utility :refer [remove-nils build-config]]))

(def config {:port 3004
             :host "127.0.0.1"
             :env :prod})

(def relevant-keys [:port :host :env])

(defn get-env-config []
  {:port (System/getenv "CRUD_PORT")
   :host (System/getenv "CRUD_HOST")
   :env (System/getenv "CRUD_ENV")})

(defn get-args-config [{port :crud-port host :crud-host env :crud-env}]
  {:port port :host host :env env})

(defn get-config [args]
  (build-config
   relevant-keys
   config
   (get-env-config)
   (get-args-config args)))
