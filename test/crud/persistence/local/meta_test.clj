(ns crud.persistence.local.meta-test
  (:require [crud.persistence.local.meta :as local]
            [crud.persistence.local.setups :refer :all]
            [clojure.test :refer [deftest is]]))

(deftest get-endpoints-success-single
  (let [db (atom storage-single-entry)
        userId (first ids)
        [data error] (local/get-endpoints db userId)]
    (is (and data (not error)))
    (is (= 1 (count data)))))

(deftest get-endpoints-success-multi
  (let [db (atom storage-multi-entry)
        userId (first ids)
        [data error] (local/get-endpoints db userId)]
    (is (and data (not error)))
    (is (= 2 (count data)))))

(deftest get-endpoints-no-endpoints
  (let [db (atom storage-no-endpoints)
        userId (first ids)
        [data error] (local/get-endpoints db userId)]
    (is (and data (not error)))
    (is (empty? data))))

(deftest get-endpoint-by-id-success-single
  (let [db (atom storage-single-entry)
        userId (first ids)
        endpointId (second ids)
        [data error] (local/get-endpoint-by-id db userId endpointId)]
    (is (and data (not error)))))

(deftest get-endpoint-by-id-success-multi
  (let [db (atom storage-multi-entry)
        userId (first ids)
        endpointId (second ids)
        [data error] (local/get-endpoint-by-id db userId endpointId)]
    (is (and data (not error)))))

(deftest get-endpoint-by-id-404-userId
  (let [db (atom storage-multi-entry)
        userId (nth ids 5)
        endpointId (nth ids 6)
        [data error] (local/get-endpoint-by-id db userId endpointId)]
    (is (and error (not data)))))

(deftest get-endpoint-by-id-404-endpointId
  (let [db (atom storage-multi-entry)
        userId (first ids)
        endpointId (nth ids 3)
        [data error] (local/get-endpoint-by-id db userId endpointId)]
    (is (and error (not data)))))

(deftest delete-endpoint-by-id-success-single
  (let [db (atom storage-single-entry)
        userId (first ids)
        endpointId (second ids)
        [{id :endpointId} error] (local/delete-endpoint-by-id db userId endpointId)]
    (is (and id (not error)))
    (is (empty? (get-in @db [:endpoints userId])))))

(deftest delete-endpoint-by-id-success-multi
  (let [db (atom storage-multi-entry)
        userId (first ids)
        endpointId (second ids)
        [{id :endpointId} error] (local/delete-endpoint-by-id db userId endpointId)]
    (is (and id (not error)))
    (is (= 1 (count (get-in @db [:endpoints userId]))))))

(deftest delete-endpoint-by-id-404-endpointId
  (let [db (atom storage-multi-entry)
        userId (first ids)
        endpointId (keyword (str (System/currentTimeMillis)))
        [data error] (local/delete-endpoint-by-id db userId endpointId)]
    (is (and error (not data)))
    (is (= 2 (count (get-in @db [:endpoints userId]))))))

(deftest delete-endpoints-by-userId-success-single
  (let [db (atom storage-single-entry)
        userId (first ids)
        [{userId :userId} error] (local/delete-endpoints-by-userId db userId)]
    (is (and userId (not error)))
    (is (empty? (get-in @db [:endpoints userId])))))

(deftest delete-endpoints-by-userId-success-multi
  (let [db (atom storage-multi-entry)
        userId (first ids)
        [{userId :userId} error] (local/delete-endpoints-by-userId db userId)]
    (is (and userId (not error)))
    (is (empty? (get-in @db [:endpoints userId])))))

(deftest delete-endpoints-by-userId-404-user
  (let [db (atom storage-multi-entry)
        userId (nth ids 7)
        [{userId :userId} error] (local/delete-endpoints-by-userId db userId)]
    (is (and userId (not error)))
    (is (= 2 (count (get-in @db [:endpoints (first ids)]))))))

(deftest update-endpoint-by-id-success-single
  (let [db (atom storage-single-entry)
        userId (first ids)
        endpointId (second ids)
        data (assoc
              (get-in
              storage-single-entry
              [:endpoints (first ids) (first endpoint-names)])
              :timestamp
              1337)
        [{id :endpointId} error] (local/update-endpoint-by-id db userId endpointId data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:endpoints (first ids) (first endpoint-names)])))))

(deftest update-endpoint-by-id-success-multi
  (let [db (atom storage-multi-entry)
        userId (first ids)
        endpointId (second ids)
        data (assoc
              (get-in
              storage-multi-entry
              [:endpoints (first ids) (second endpoint-names)])
              :timestamp
              1337)
        [{id :endpointId} error] (local/update-endpoint-by-id db userId endpointId data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:endpoints (first ids) (second endpoint-names)])))))

(deftest update-endpoint-by-id-404-endpointId
  (let [db (atom storage-multi-entry)
        userId (first ids)
        endpointId (keyword (str (System/currentTimeMillis)))
        data (assoc
              (get-in
              storage-multi-entry
              [:endpoints (first ids) (second endpoint-names)])
              :timestamp
              1337)
        data-before @db
        [data error] (local/update-endpoint-by-id db userId endpointId data)
        data-after @db]
    (is (and error (not data)))
    (is (= data-before data-after))))
