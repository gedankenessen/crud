(ns crud.persistence.atom.meta-test
  (:require [crud.persistence.atom.meta :as atom]
            [crud.persistence.atom.setups :refer :all]
            [clojure.test :refer [deftest is]]))

(deftest get-endpoints-success-single
  (let [atom (atom storage-single-entry)
        userId (first ids)
        [data error] (atom/get-endpoints atom userId)]
    (is (and data (not error)))
    (is (= 1 (count data)))))

(deftest get-endpoints-success-multi
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        [data error] (atom/get-endpoints atom userId)]
    (is (and data (not error)))
    (is (= 2 (count data)))))

(deftest get-endpoints-no-endpoints
  (let [atom (atom storage-no-endpoints)
        userId (first ids)
        [data error] (atom/get-endpoints atom userId)]
    (is (and data (not error)))
    (is (empty? data))))

(deftest get-endpoint-by-id-success-single
  (let [atom (atom storage-single-entry)
        userId (first ids)
        endpointId (second ids)
        [data error] (atom/get-endpoint-by-id atom userId endpointId)]
    (is (and data (not error)))))

(deftest get-endpoint-by-id-success-multi
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        endpointId (second ids)
        [data error] (atom/get-endpoint-by-id atom userId endpointId)]
    (is (and data (not error)))))

(deftest get-endpoint-by-id-404-userId
  (let [atom (atom storage-multi-entry)
        userId (nth ids 5)
        endpointId (nth ids 6)
        [data error] (atom/get-endpoint-by-id atom userId endpointId)]
    (is (and error (not data)))))

(deftest get-endpoint-by-id-404-endpointId
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        endpointId (nth ids 3)
        [data error] (atom/get-endpoint-by-id atom userId endpointId)]
    (is (and error (not data)))))

(deftest delete-endpoint-by-id-success-single
  (let [atom (atom storage-single-entry)
        userId (first ids)
        endpointId (second ids)
        [{id :endpointId} error] (atom/delete-endpoint-by-id atom userId endpointId)]
    (is (and id (not error)))
    (is (empty? (get-in @atom [:endpoints userId])))))

(deftest delete-endpoint-by-id-success-multi
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        endpointId (second ids)
        [{id :endpointId} error] (atom/delete-endpoint-by-id atom userId endpointId)]
    (is (and id (not error)))
    (is (= 1 (count (get-in @atom [:endpoints userId]))))))

(deftest delete-endpoint-by-id-404-endpointId
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        endpointId (keyword (str (System/currentTimeMillis)))
        [data error] (atom/delete-endpoint-by-id atom userId endpointId)]
    (is (and error (not data)))
    (is (= 2 (count (get-in @atom [:endpoints userId]))))))

(deftest delete-endpoints-by-userId-success-single
  (let [atom (atom storage-single-entry)
        userId (first ids)
        [{userId :userId} error] (atom/delete-endpoints-by-userId atom userId)]
    (is (and userId (not error)))
    (is (empty? (get-in @atom [:endpoints userId])))))

(deftest delete-endpoints-by-userId-success-multi
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        [{userId :userId} error] (atom/delete-endpoints-by-userId atom userId)]
    (is (and userId (not error)))
    (is (empty? (get-in @atom [:endpoints userId])))))

(deftest delete-endpoints-by-userId-404-user
  (let [atom (atom storage-multi-entry)
        userId (nth ids 7)
        [{userId :userId} error] (atom/delete-endpoints-by-userId atom userId)]
    (is (and userId (not error)))
    (is (= 2 (count (get-in @atom [:endpoints (first ids)]))))))

(deftest update-endpoint-by-id-success-single
  (let [atom (atom storage-single-entry)
        userId (first ids)
        endpointId (second ids)
        data (assoc
              (get-in
              storage-single-entry
              [:endpoints (first ids) (first endpoint-names)])
              :timestamp
              1337)
        [{id :endpointId} error] (atom/update-endpoint-by-id atom userId endpointId data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:endpoints (first ids) (first endpoint-names)])))))

(deftest update-endpoint-by-id-success-multi
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        endpointId (second ids)
        data (assoc
              (get-in
              storage-multi-entry
              [:endpoints (first ids) (second endpoint-names)])
              :timestamp
              1337)
        [{id :endpointId} error] (atom/update-endpoint-by-id atom userId endpointId data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:endpoints (first ids) (second endpoint-names)])))))

(deftest update-endpoint-by-id-404-endpointId
  (let [atom (atom storage-multi-entry)
        userId (first ids)
        endpointId (keyword (str (System/currentTimeMillis)))
        data (assoc
              (get-in
              storage-multi-entry
              [:endpoints (first ids) (second endpoint-names)])
              :timestamp
              1337)
        data-before @atom
        [data error] (atom/update-endpoint-by-id atom userId endpointId data)
        data-after @atom]
    (is (and error (not data)))
    (is (= data-before data-after))))
