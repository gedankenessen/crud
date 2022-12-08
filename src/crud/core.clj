(ns crud.core
  (:gen-class)
  (:require [crud.entrypoint.core :refer :all]
            [crud.config :refer [config]]))

(defn -main [& args]
  (start-server (merge args config)))

(comment
  (def server (atom (start-server config)))
  @server
  ;; TODO: Make dev ns
  #_(server/server-stop! @server))

