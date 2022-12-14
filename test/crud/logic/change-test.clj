(ns crud.logic.change-test
  (:require
   [clojure.walk :refer :all]
   [clojure.data :refer :all]
   [clojure.test :refer :all]
   [crud.logic.change :refer [has-changed?]]))

(def product-1 {:name "Table" :color "brown"})
(def product-2 (assoc product-1 :size "big"))
(def product-nil (assoc product-1 :size nil))
(def product-nested-1 (assoc product-1 :size {:x 120 :y 60 :z 30}))
(def product-nested-2 (assoc product-1 :size {:x 210 :y 40 :z 80}))
(def product-nested-3 (assoc product-1 :size {:width 120 :length 60 :height 30}))
(def product-nested-4 (assoc product-1 :size {:width 210}))
(def product-nested-nil (assoc product-1 :size {:width nil :length nil :height nil}))
(def product-nested-nil-some (assoc product-1 :size {:width 120 :length nil :height nil}))

(deftest no-changes
  (is (not (has-changed? product-1 product-1))))

(deftest some-changes
  (is (has-changed? product-1 product-2)))

(deftest ignore-nil
  (is (not (has-changed? product-1 product-nil))))

(deftest nested-changes
  (is (has-changed? product-nested-1 product-nested-3)))

(deftest nested-no-changes
  (is (not (has-changed? product-nested-1 product-nested-2))))

(deftest nested-some-nil-no-changes
  (is (not (has-changed? product-nested-4 product-nested-nil-some))))

(deftest nested-some-nil-some-changes
  (is (has-changed? product-2 product-nested-nil-some)))

(deftest nested-nil-no-changes
  (is (not (has-changed? product-1 product-nested-nil))))

(deftest nested-nil-some-changes
  (is (has-changed? product-2 product-nested-nil)))
