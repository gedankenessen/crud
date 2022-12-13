(ns dev.core
  (:require [crud.entrypoint.core :refer :all]
            [org.httpkit.server :as server]
            [crud.config :refer [config]]))

(comment
  (def server (atom (start-server config)))
  @server
  (server/server-stop! @server))
