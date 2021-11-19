(ns monmon.persistence-test
  (:require [monmon.persistence :as persistence]
            [clojure.test :as t]))


;; REPL tests; no unit test!

(defn get-db []
  (persistence/get-connection "crud"))

(defn test-create-endpoint []
  (persistence/create-endpoint
   (get-db)
   "61967b528c1b2d65e15b9717"
   "items"
   persistence/all-methods
   {:name "brita wasserfilter"
    :width 12.5
    :height 45
    :unit "cm"}))

(defn test-add-endpoint-version []
  (persistence/add-endpoint-version
   (get-db)
   "6196b76fd008dc356948bed6"
   "items"
   persistence/all-methods
   {:name "ikea table",
    :width 125,
    :height 80,
    :unit "cm"
    :color "bamboo"}))
