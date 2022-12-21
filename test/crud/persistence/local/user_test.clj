(ns crud.persistence.local.user-test
  (:require [clojure.test :refer [deftest is]]
            [crud.persistence.local.setups :refer :all]
            [crud.persistence.local.user :as local]))

(deftest get-user-by-email-success-single-entry
  (let [db (atom storage-single-entry)
        user demo-user-1
        id (first ids)
        email (:email user)
        [data error] (local/get-user-by-email db email)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest get-user-by-email-success-multi-entry
  (let [db (atom storage-multi-entry)
        user demo-user-2
        id (nth ids 10)
        email (:email user)
        [data error] (local/get-user-by-email db email)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest get-user-by-email-404-user
  (let [db (atom storage-multi-entry)
        email (:email demo-user-3)
        [data error] (local/get-user-by-email db email)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-user-by-id-success-single-entry
  (let [db (atom storage-single-entry)
        user demo-user-1
        id (first ids)
        [data error] (local/get-user-by-id db id)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest get-user-by-id-success-single-entry
  (let [db (atom storage-multi-entry)
        user demo-user-1
        id (first ids)
        [data error] (local/get-user-by-id db id)]
    (is (and data (not error)))
    (is (= data (assoc user :_id id)))))

(deftest add-user-success-empty
  (let [db (atom storage-empty)
        user demo-user-2
        [{id :id} error] (local/add-user db user)]
    (is (and id (not error)))
    (is (= user (get-in @db [:users id])))))

(deftest add-user-success-single-entry
  (let [db (atom storage-single-entry)
        user demo-user-2
        [{id :id} error] (local/add-user db user)]
    (is (and id (not error)))
    (is (= user (get-in @db [:users id])))
    (is (= 2 (count (:users @db))))))

(deftest add-user-success-multi-entry
  (let [db (atom storage-multi-entry)
        user demo-user-3
        [{id :id} error] (local/add-user db user)]
    (is (and id (not error)))
    (is (= user (get-in @db [:users id])))
    (is (= 3 (count (:users @db))))))

(deftest update-user-success-single-entry
  (let [db (atom storage-single-entry)
        user demo-user-1
        id (first ids)
        data (assoc user :name "Mike")
        [{id :id} error] (local/update-user db id data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:users id])))))

(deftest update-user-success-multi-entry
  (let [db (atom storage-multi-entry)
        user demo-user-1
        id (first ids)
        data (assoc user :name "Mike")
        [{id :id} error] (local/update-user db id data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:users id])))
    (is (= 2 (count (:users @db))))))

(deftest update-user-404-user
  (let [db (atom storage-single-entry)
        user demo-user-1
        id (keyword (System/currentTimeMillis))
        data (assoc user :name "Mike")
        [{id :id} error] (local/update-user db id data)]
    ;; Action unsuccessful?
    (is (and error (not id)))
    ;; Did user stay the same?
    (is (= user (get-in @db [:users (first ids)])))
    ;; Was no user added?
    (is (= 1 (count (:users @db))))))

(deftest delete-user-success-single-entry
  (let [db (atom storage-single-entry)
        id (first ids)
        [{id :id} error] (local/delete-user db id)]
    (is (and id (not error)))
    (is (empty? (:users @db)))))

(deftest delete-user-success-multi-entry
  (let [db (atom storage-multi-entry)
        id (first ids)
        [{id :id} error] (local/delete-user db id)]
    (is (and id (not error)))
    (is (nil? (get-in @db [:users id])))
    (is (= 1 (count (:users @db))))))

(deftest delete-user-404-user
  (let [db (atom storage-single-entry)
        id (keyword (System/currentTimeMillis))
        [{id :id} error] (local/delete-user db id)]
    (is (and error (not id)))
    (is (= demo-user-1 (get-in @db [:users (first ids)])))
    (is (= 1 (count (:users @db))))))


