(ns crud.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [crud.entrypoint.core :refer :all]
            [crud.config :refer [get-config]]))

(def cli-options
  [["-murs" "--mongo-user USER" "User string"
    :parse-fn #(str %)]
   ["-madb" "--mongo-auth-db DB" "DB string"
    :parse-fn #(str %)]
   ["-mpw" "--mongo-password password" "Password string"
    :parse-fn #(str %)]
   ["-mdb" "--mongo-db DB" "DB string"
    :parse-fn #(str %)]
   ["-muri" "--mongo-url URL" "DB location"
    :parse-fn #(str %)]
   ["-mport" "--mongo-port PORT" "DB port"
    :parse-fn #(Integer/parseInt %)]
   ["-mauth?" "--mongo-should-auth BOOLEAN" "Should crud auth with db?"
    :parse-fn #(read-string %)]
   ["-mauthdb" "--mongo-auth-db STRING" "Auth DB"
    :parse-fn #(read-string %)]
   ["-cport" "--crud-port PORT" "CRUD port"
    :parse-fn #(Integer/parseInt %)]
   ["-chost" "--crud-host HOST" "CRUD host"
    :parse-fn #(str %)]
   ["-cenv" "--crud-env env" "CRUD env"
    :parse-fn #(keyword %)]
   ["-csecret" "--crud-token-secret SECRET" "Token secret"
    :parse-fn #(str %)]
   ["-cversion" "--crud-token-version version" "Token version"
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]])

(defn -main [& args]
  (start-server (get-config (:options (parse-opts args cli-options)))))
