(ns dev.mongo
  (:require [crud.persistence.mongo.config :as config]
            [crud.persistence.mongo.core :as core]
            [crud.persistence.protocol :as prot]))

(def email "marlon@gedankenessen.de")

(def cfg (core/connect config/config))

(prot/get-user-by-email cfg email)

