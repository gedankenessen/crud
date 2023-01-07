(ns crud.persistence.local.crud-test
  (:require
   [clojure.test :refer [deftest is]]
   [crud.persistence.local.setups :refer :all]
   [crud.persistence.local.crud :as local]))

(deftest get-data-single-entry
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected [(assoc demo-data-1 :_id (nth ids 3))]
        [data error] (local/get-data db user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-multi-entry
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected [(assoc demo-data-1 :_id (nth ids 7))
                  (assoc demo-data-2 :_id (nth ids 8))
                  (assoc demo-data-3 :_id (nth ids 9))]
        [data error] (local/get-data db user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-empty
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (local/get-data db user endpoint)]
    (is (and data (not error)))
    (is (and (vector? data) (empty? data)))))

(deftest get-data-no-endpoints
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        expected nil
        [data error] (local/get-data db user endpoint)]
    (is (and data (not error)))
    (is (and (vector? data) (empty? data)))))

(deftest get-data-no-user
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        expected nil
        [data error] (local/get-data db user endpoint)]
    (is (and data (not error)))
    (is (and (vector? data) (empty? data)))))

(deftest get-data-id-success-single-entry
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        expected (assoc demo-data-1 :_id (nth ids 3))
        [data error] (local/get-data-by-id db user endpoint id)]
    (is (and data (not error)))
    (is (= data expected))))

(deftest get-data-id-success-multi-entry
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 7)
        expected (assoc demo-data-1 :_id (nth ids 7))
        [data error] (local/get-data-by-id db user endpoint id)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-id-404-wrong-id
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (keyword (System/currentTimeMillis))
        [data error] (local/get-data-by-id db user endpoint id)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-id-404-no-endpoint
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint :donuts
        id (nth ids 7)
        [data error] (local/get-data-by-id db user endpoint id)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-id-404-no-user
  (let [db (atom storage-no-endpoints)
        user (keyword (System/currentTimeMillis))
        endpoint (first endpoint-names)
        id (nth ids 7)
        [data error] (local/get-data-by-id db user endpoint id)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-last-success
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected (assoc demo-data-1 :_id (nth ids 7))
        [data error] (local/get-data-last db user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-last-success-single
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected (assoc demo-data-1 :_id (nth ids 3))
        [data error] (local/get-data-last db user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-last-no-data
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (local/get-data-last db user endpoint)]
    (is (and (not data) (not error)))))

(deftest get-data-last-no-endpoint
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (local/get-data-last db user endpoint)]
    (is (and (not data) (not error)))))

(deftest get-data-last-empty
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (local/get-data-last db user endpoint)]
    (is (and (not data) (not error)))))

(deftest add-endpoint-success
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-1
        [{endpoint-id :endpoint-id
          data-id :data-id, :as data} error] (local/add-endpoint db user endpoint data)
        endpoint-after (get-in @db [:endpoints user endpoint])]
    ;; Action successful?
    (is (and data (not error)))
    ;; Ids not nil?
    (is (and endpoint-id data-id))
    ;; Added successfully?
    (is endpoint-after)
    ;; Endpoint-id correctly set?
    (is (= (:_id endpoint-after) endpoint-id))
    ;; Values set correctly?
    (is (= (get-in endpoint-after [:data data-id])
           demo-data-1))))

(deftest add-endpoint-success-multiple-present
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint :test
        data {:message "Testing the endpoint"}
        [{endpoint-id :endpoint-id
          data-id :data-id, :as resp}  error] (local/add-endpoint db user endpoint data)]
    (is (and resp (not error)))
    (is (and endpoint-id data-id))
    (is (= 3 (count (get-in @db [:endpoints user]))))))

(deftest add-endpoint-storage-empty
  (let [db (atom storage-empty)
        user (first ids)
        endpoint :test
        data {:message "Testing the endpoint"}
        [{endpoint-id :endpoint-id
          data-id :data-id, :as resp} error] (local/add-endpoint db user endpoint data)]
    ;; Action successful?
    (is (and resp (not error)))
    ;; Ids not nil?
    (is (and endpoint-id data-id))
    ;; Data in there?
    (is (= data (get-in @db [:endpoints user endpoint :data data-id])))))

(deftest add-endpoint-already-exists
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data {:message "Testing the endpoint"}
        [{endpoint-id :endpoint-id
          data-id :data-id, :as resp} error] (local/add-endpoint db user endpoint data)]
    ;; Action successful?
    (is (and resp (not error)))
    ;; Ids not nil?
    (is (and endpoint-id data-id))
    ;; Is data there?
    (is (= data (get-in @db [:endpoints user endpoint :data data-id])))))

(deftest add-data-success-single-entry
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-2
        [{id :data-id} error] (local/add-data db user endpoint data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:endpoints user endpoint :data id])))
    (is (= 2 (count (get-in @db [:endpoints user endpoint :data]))))))

(deftest add-data-sucess-multi-entry
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data {:x (Math/random) :y (Math/random)}
        [{id :data-id } error] (local/add-data db user endpoint data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:endpoints user endpoint :data id])))
    ;; Correct amount of data in datas?
    (is (= 4 (count (get-in @db [:endpoints user endpoint :data]))))))

(deftest add-data-empty-storage
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names )
        data demo-data-1
        [data error] (local/add-data db user endpoint data)]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Storage still empty?
    (is (= {} @db))))

(deftest add-data-no-endpoints
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-1
        data-before @db
        [data error] (local/add-data db user endpoint data)
        data-after @db]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Storage still empty?
    (is (= data-before data-after))))

(deftest add-version-single-entry
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-4
        [{id :data-id} error] (local/add-version db user endpoint data)]
    (is (and id (not error)))
    (is (= data (get-in @db [:endpoints user endpoint :data id])))
    (is (= 1 (count (get-in @db [:endpoints user endpoint :data]))))))

(deftest add-version-multi-entry
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data {:foo "bar"}
        [{id :data-id} error] (local/add-version db user endpoint data)]
    (is (and id (not error)))
    ;; Is data set correctly?
    (is (= data (get-in @db [:endpoints user endpoint :data id])))
    ;; Has data been cleared?
    (is (= 1 (count (get-in @db [:endpoints user endpoint :data]))))
    ;; Is other endpoint stil there?
    (is (= 2 (count (get-in @db [:endpoints user]))))))

(deftest add-version-empty-storage
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        data {:x 701 :y 401}
        [data error] (local/add-version db user endpoint data)]
    ;; Action unsuccessful?
    (is (and (not data) error))
    ;; Storage still empty?
    (is (= @db {}))))

(deftest add-version-no-endpoints
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        data {:x 701 :y 401}
        data-before @db
        [data error] (local/add-version db user endpoint data)
        data-after @db]
    ;; Action unsuccessful?
    (is (and (not data) error))
    ;; Storage still empty?
    (is (= data-before data-after))))

(deftest delete-data-by-id-success-single-entry
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        [{id :id} error] (local/delete-data-by-id db user endpoint id)]
    ;; Action successful?
    (is (and id (not error)))
    (is (empty? (get-in @db [:endpoints user endpoint :data])))))

(deftest delete-data-by-id-success-multi-entry
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 7)
        [{id :id} error] (local/delete-data-by-id db user endpoint id)]
    ;; Action successful?
    (is (and id (not error)))
    (is (nil? (get-in @db [:endpoints user endpoint :data id])))
    (is (= 2 (count (get-in @db [:endpoints user endpoint :data]))))))

(deftest delete-data-by-id-non-existent-id
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (keyword (System/currentTimeMillis))
        data-before @db
        [data error] (local/delete-data-by-id db user endpoint id)
        data-after @db]
    ;; Action successful?
    (is (and error (not data)))
    ;; Is user still there?
    (is (= data-before data-after))))

(deftest delete-data-by-id-non-existent-user
  (let [db (atom storage-single-entry)
        user (keyword (System/currentTimeMillis))
        endpoint (first endpoint-names)
        id (nth ids 3)
        data-before @db
        [data  error] (local/delete-data-by-id db user endpoint id)
        data-after @db]
    (is (and error (not data)))
    (is (= data-before data-after))))

(deftest delete-data-by-id-non-existent-endpoint
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint :test
        id (nth ids 3)
        data-before @db
        [data error] (local/delete-data-by-id db user endpoint id)
        data-after @db]
    (is (and error (not data)))
    (is (= data-before data-after))))

(deftest delete-data-by-id-empty-storage
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        data-before @db
        [data error] (local/delete-data-by-id db user endpoint id)
        data-after @db]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest delete-data-by-id-no-endpoints
  (let [db (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        data-before @db
        [data error] (local/delete-data-by-id db user endpoint id)
        data-after @db]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest update-data-by-id-success-single-entry
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data demo-data-2
        data-before (get-in @db [:endpoints user endpoint :data])
        [{id :id} error] (local/update-data-by-id db user endpoint id new-data)
        data-after (get-in @db [:endpoints user endpoint :data])]
    ;; Action successful?
    (is (and id (not error)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (not (= data-before data-after)))
    ;; Is (new)-data correct?
    (is (= (get-in data-after [id]) new-data))))


(deftest update-data-by-id-success-multi-entry
  (let [db (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 7)
        new-data {:x 1 :y 1}
        data-before (get-in @db [:endpoints user endpoint :data])
        [{id :id} error] (local/update-data-by-id db user endpoint id new-data)
        data-after (get-in @db [:endpoints user endpoint :data])]
    ;; Action successful?
    (is (and id (not error)))
    ;; Is data still there?
    (is (= 3 (count data-after)))
    ;; Is data different?
    (is (not (= data-before data-after)))
    ;; Is (new)-data correct?
    (is (= (get-in data-after [(keyword id)]) new-data))))

(deftest update-data-by-id-non-existent-id
  (let [db (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (keyword (System/currentTimeMillis))
        new-data {:x 1 :y 1}
        data-before (get-in @db [:endpoints user endpoint :data])
        [data  error] (local/update-data-by-id db user endpoint id new-data)
        data-after (get-in @db [:endpoints user endpoint :data])]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (= data-before data-after))))

(deftest update-data-by-id-non-existent-user
  (let [db (atom storage-single-entry)
        user (keyword (System/currentTimeMillis))
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data {:x 1 :y 1}
        data-before @db
        [data error] (local/update-data-by-id db user endpoint id new-data)
        data-after @db]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Is data different?
    (is (= data-before data-after))))

(deftest update-data-by-id-empty-storage
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data {:x 1 :y 1}
        data-before @db
        [data error] (local/update-data-by-id db user endpoint id new-data)
        data-after @db]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest update-data-by-id-no-endpoints
  (let [db (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data {:x 1 :y 1}
        data-before @db
        [data error] (local/update-data-by-id db user endpoint id new-data)
        data-after @db]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

