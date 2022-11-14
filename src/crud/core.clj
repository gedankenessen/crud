(ns crud.core
  (:gen-class)
  (:require [crud.entrypoint :as entrypoint]))

(defn -main [& args]
  entrypoint/start-server)

