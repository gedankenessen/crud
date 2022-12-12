(ns crud.entrypoint.config)

(def config {:port (or (System/getenv "CRUD_PORT") 3004)
             :host "127.0.0.1"
             :env :prod})

