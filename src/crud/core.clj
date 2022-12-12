(ns crud.core
  (:gen-class)
  (:require [crud.entrypoint.core :refer :all]
            [crud.config :refer [config]]))

(defn -main [& args]
  (start-server (merge args config)))
