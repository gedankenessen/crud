(ns crud.persistence.atom.user-test
  (:require [clojure.test :refer [deftest is]]
            [crud.persistence.atom.setups :refer :all]
            [crud.persistence.atom.user :as atom]))

(deftest get-user-by-email-success-single-entry
  (let [atom (atom storage-single-entry)
        user demo-user-1
        id (first ids)
        email (:email user)
        [data error] (atom/get-user-by-email atom email)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest get-user-by-email-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user demo-user-2
        id (nth ids 10)
        email (:email user)
        [data error] (atom/get-user-by-email atom email)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest get-user-by-email-404-user
  (let [atom (atom storage-multi-entry)
        email (:email demo-user-3)
        [data error] (atom/get-user-by-email atom email)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-user-by-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user demo-user-1
        id (first ids)
        [data error] (atom/get-user-by-id atom id)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest get-user-by-id-success-single-entry
  (let [atom (atom storage-multi-entry)
        user demo-user-1
        id (first ids)
        [data error] (atom/get-user-by-id atom id)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest add-user-success-empty
  (let [atom (atom storage-empty)
        user demo-user-2
        [{id :id} error] (atom/add-user atom user)]
    (is (and id (not error)))
    (is (= user (get-in @atom [:users id])))))

(deftest add-user-success-single-entry
  (let [atom (atom storage-single-entry)
        user demo-user-2
        [{id :id} error] (atom/add-user atom user)]
    (is (and id (not error)))
    (is (= user (get-in @atom [:users id])))
    (is (= 2 (count (:users @atom))))))

(deftest add-user-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user demo-user-3
        [{id :id} error] (atom/add-user atom user)]
    (is (and id (not error)))
    (is (= user (get-in @atom [:users id])))
    (is (= 3 (count (:users @atom))))))

(deftest update-user-success-single-entry
  (let [atom (atom storage-single-entry)
        user demo-user-1
        id (first ids)
        data (assoc user :name "Mike")
        [{id :id} error] (atom/update-user atom id data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:users id])))))

(deftest update-user-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user demo-user-1
        id (first ids)
        data (assoc user :name "Mike")
        [{id :id} error] (atom/update-user atom id data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:users id])))
    (is (= 2 (count (:users @atom))))))

(deftest update-user-404-user
  (let [atom (atom storage-single-entry)
        user demo-user-1
        id (keyword (System/currentTimeMillis))
        data (assoc user :name "Mike")
        [{id :id} error] (atom/update-user atom id data)]
    ;; Action unsuccessful?
    (is (and error (not id)))
    ;; Did user stay the same?
    (is (= user (get-in @atom [:users (first ids)])))
    ;; Was no user added?
    (is (= 1 (count (:users @atom))))))

(deftest delete-user-success-single-entry
  (let [atom (atom storage-single-entry)
        id (first ids)
        [{id :id} error] (atom/delete-user atom id)]
    (is (and id (not error)))
    (is (empty? (:users @atom)))))

(deftest delete-user-success-multi-entry
  (let [atom (atom storage-multi-entry)
        id (first ids)
        [{id :id} error] (atom/delete-user atom id)]
    (is (and id (not error)))
    (is (nil? (get-in @atom [:users id])))
    (is (= 1 (count (:users @atom))))))

(deftest delete-user-404-user
  (let [atom (atom storage-single-entry)
        id (keyword (System/currentTimeMillis))
        [{id :id} error] (atom/delete-user atom id)]
    (is (and error (not id)))
    (is (= demo-user-1 (get-in @atom [:users (first ids)])))
    (is (= 1 (count (:users @atom))))))


