(ns dev.handler
  (:require [crud.entrypoint.core :refer :all]
            [crud.config :refer [config get-config]]
            [ring.mock.request :as mock]
            [crud.entrypoint.tokens :as tokens]))

(def db (atom {}))

(let [handler (build-routes (get-config {:default-db db}))]
  (handler (-> (mock/request :post "/user/register")
               (mock/json-body {:email "a" :password "b" :name "Marlon"}))))

(let [handler (build-routes (get-config {:default-db db}))]
  (handler (-> (mock/request :post "/user/login")
               (mock/json-body {:email "a" :password "b"}))))

(def token "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI1NTFjZDRlYi0wMTc1LTQwMmYtOTBlOS03ZWQzYjk3ZjVkMzgiLCJ2ZXIiOjAsImlhdCI6MTY3MTY0MDQyNjA3MCwiZXhwIjoxNjc0MjMyNDI2MDcwfQ.HpJ4aXmnWwf3XYTwY7dFdSZ0KbTq7UuBwLYJfJNxEN0")

(let [handler (build-routes (get-config {:default-db db}))]
  (handler (-> (mock/request :post "/build/points")
               (mock/header "Authorization" (str token))
               (mock/json-body {:x (Math/random) :y (Math/random) :z (Math/random)}))))

(let [handler (build-routes (get-config {:default-db db}))]
  (handler (-> (mock/request :post "/build/words")
               (mock/header "Authorization" token)
               (mock/json-body {:word "Voiture" :lang :fr :difficulty 2}))))

@db

(reset! db {:users (:users @db) :endpoints {}})
