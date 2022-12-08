(ns crud.core
  (:gen-class)
  (:require [crud.entrypoint.core :refer :all]))

(defn -main [& args]
  (start-server args))

