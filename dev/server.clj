(ns dev.core
  (:require [crud.entrypoint.core :refer :all]
            [org.httpkit.server :as server]
            [crud.config :refer [config get-config]]
            [ring.mock.request :as mock]
            [crud.entrypoint.tokens :as tokens]))

(comment
  (def server (atom (start-server (get-config {}))))
  @server
  (server/server-stop! @server))
