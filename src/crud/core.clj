(ns crud.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [crud.entrypoint.core :refer :all]
            [crud.config :refer [get-config cli-options]]))

(defn -main [& args]
  (start-server (get-config (:options (parse-opts args cli-options)))))
