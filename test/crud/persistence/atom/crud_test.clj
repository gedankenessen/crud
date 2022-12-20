(ns crud.persistence.atom.crud-test
  (:require
   [clojure.test :refer [deftest is]]
   [crud.persistence.atom.setups :refer :all]
   [crud.persistence.atom.crud :as atom]))

(deftest get-data-single-entry
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected [(assoc demo-data-1 :_id (nth ids 3))]
        [data error] (atom/get-data atom user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-multi-entry
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected [(assoc demo-data-1 :_id (nth ids 7))
                  (assoc demo-data-2 :_id (nth ids 8))
                  (assoc demo-data-3 :_id (nth ids 9))]
        [data error] (atom/get-data atom user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-empty
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (atom/get-data atom user endpoint)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        expected nil
        [data error] (atom/get-data atom user endpoint)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-no-user
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        expected nil
        [data error] (atom/get-data atom user endpoint)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        expected (assoc demo-data-1 :_id (nth ids 3))
        [data error] (atom/get-data-by-id atom user endpoint id)]
    (is (and data (not error)))
    (is (= data expected))))

(deftest get-data-id-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 7)
        expected (assoc demo-data-1 :_id (nth ids 7))
        [data error] (atom/get-data-by-id atom user endpoint id)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-id-404-wrong-id
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (keyword (System/currentTimeMillis))
        [data error] (atom/get-data-by-id atom user endpoint id)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-id-404-no-endpoint
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint :donuts
        id (nth ids 7)
        [data error] (atom/get-data-by-id atom user endpoint id)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-id-404-no-user
  (let [atom (atom storage-no-endpoints)
        user (keyword (System/currentTimeMillis))
        endpoint (first endpoint-names)
        id (nth ids 7)
        [data error] (atom/get-data-by-id atom user endpoint id)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-last-success
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected (assoc demo-data-1 :_id (nth ids 7))
        [data error] (atom/get-data-last atom user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-last-success-single
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        expected (assoc demo-data-1 :_id (nth ids 3))
        [data error] (atom/get-data-last atom user endpoint)]
    (is (and data (not error)))
    (is (= expected data))))

(deftest get-data-last-no-data
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (atom/get-data-last atom user endpoint)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-last-no-endpoint
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (atom/get-data-last atom user endpoint)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest get-data-last-empty
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        [data error] (atom/get-data-last atom user endpoint)]
    (is (and error (not data)))
    (is (= 404 (:status error)))))

(deftest add-endpoint-success
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-1
        [{endpoint-id :endpoint-id
          data-id :data-id, :as data} error] (atom/add-endpoint atom user endpoint data)
        endpoint-after (get-in @atom [:endpoints user endpoint])]
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
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint :test
        data {:message "Testing the endpoint"}
        [{endpoint-id :endpoint-id
          data-id :data-id, :as resp}  error] (atom/add-endpoint atom user endpoint data)]
    (is (and resp (not error)))
    (is (and endpoint-id data-id))
    (is (= 3 (count (get-in @atom [:endpoints user]))))))

(deftest add-endpoint-storage-empty
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint :test
        data {:message "Testing the endpoint"}
        [{endpoint-id :endpoint-id
          data-id :data-id, :as resp} error] (atom/add-endpoint atom user endpoint data)]
    ;; Action successful?
    (is (and resp (not error)))
    ;; Ids not nil?
    (is (and endpoint-id data-id))
    ;; Data in there?
    (is (= data (get-in @atom [:endpoints user endpoint :data data-id])))))

(deftest add-endpoint-already-exists
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data {:message "Testing the endpoint"}
        [{endpoint-id :endpoint-id
          data-id :data-id, :as resp} error] (atom/add-endpoint atom user endpoint data)]
    ;; Action successful?
    (is (and resp (not error)))
    ;; Ids not nil?
    (is (and endpoint-id data-id))
    ;; Is data there?
    (is (= data (get-in @atom [:endpoints user endpoint :data data-id])))))

(deftest add-data-success-single-entry
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-2
        [{id :data-id} error] (atom/add-data atom user endpoint data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:endpoints user endpoint :data id])))
    (is (= 2 (count (get-in @atom [:endpoints user endpoint :data]))))))

(deftest add-data-sucess-multi-entry
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data {:x (Math/random) :y (Math/random)}
        [{id :data-id } error] (atom/add-data atom user endpoint data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:endpoints user endpoint :data id])))
    ;; Correct amount of data in datas?
    (is (= 4 (count (get-in @atom [:endpoints user endpoint :data]))))))

(deftest add-data-empty-storage
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names )
        data demo-data-1
        [data error] (atom/add-data atom user endpoint data)]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Storage still empty?
    (is (= {} @atom))))

(deftest add-data-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-1
        data-before @atom
        [data error] (atom/add-data atom user endpoint data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Storage still empty?
    (is (= data-before data-after))))

(deftest add-version-single-entry
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data demo-data-4
        [{id :data-id} error] (atom/add-version atom user endpoint data)]
    (is (and id (not error)))
    (is (= data (get-in @atom [:endpoints user endpoint :data id])))
    (is (= 1 (count (get-in @atom [:endpoints user endpoint :data]))))))

(deftest add-version-multi-entry
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        data {:foo "bar"}
        [{id :data-id} error] (atom/add-version atom user endpoint data)]
    (is (and id (not error)))
    ;; Is data set correctly?
    (is (= data (get-in @atom [:endpoints user endpoint :data id])))
    ;; Has data been cleared?
    (is (= 1 (count (get-in @atom [:endpoints user endpoint :data]))))
    ;; Is other endpoint stil there?
    (is (= 2 (count (get-in @atom [:endpoints user]))))))

(deftest add-version-empty-storage
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        data {:x 701 :y 401}
        [data error] (atom/add-version atom user endpoint data)]
    ;; Action unsuccessful?
    (is (and (not data) error))
    ;; Storage still empty?
    (is (= @atom {}))))

(deftest add-version-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        data {:x 701 :y 401}
        data-before @atom
        [data error] (atom/add-version atom user endpoint data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and (not data) error))
    ;; Storage still empty?
    (is (= data-before data-after))))

(deftest delete-data-by-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        [{id :id} error] (atom/delete-data-by-id atom user endpoint id)]
    ;; Action successful?
    (is (and id (not error)))
    (is (empty? (get-in @atom [:endpoints user endpoint :data])))))

(deftest delete-data-by-id-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 7)
        [{id :id} error] (atom/delete-data-by-id atom user endpoint id)]
    ;; Action successful?
    (is (and id (not error)))
    (is (nil? (get-in @atom [:endpoints user endpoint :data id])))
    (is (= 2 (count (get-in @atom [:endpoints user endpoint :data]))))))

(deftest delete-data-by-id-non-existent-id
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (keyword (System/currentTimeMillis))
        data-before @atom
        [data error] (atom/delete-data-by-id atom user endpoint id)
        data-after @atom]
    ;; Action successful?
    (is (and error (not data)))
    ;; Is user still there?
    (is (= data-before data-after))))

(deftest delete-data-by-id-non-existent-user
  (let [atom (atom storage-single-entry)
        user (keyword (System/currentTimeMillis))
        endpoint (first endpoint-names)
        id (nth ids 3)
        data-before @atom
        [data  error] (atom/delete-data-by-id atom user endpoint id)
        data-after @atom]
    (is (and error (not data)))
    (is (= data-before data-after))))

(deftest delete-data-by-id-non-existent-endpoint
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint :test
        id (nth ids 3)
        data-before @atom
        [data error] (atom/delete-data-by-id atom user endpoint id)
        data-after @atom]
    (is (and error (not data)))
    (is (= data-before data-after))))

(deftest delete-data-by-id-empty-storage
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        data-before @atom
        [data error] (atom/delete-data-by-id atom user endpoint id)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest delete-data-by-id-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        data-before @atom
        [data error] (atom/delete-data-by-id atom user endpoint id)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest update-data-by-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data demo-data-2
        data-before (get-in @atom [:endpoints user endpoint :data])
        [{id :id} error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after (get-in @atom [:endpoints user endpoint :data])]
    ;; Action successful?
    (is (and id (not error)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (not (= data-before data-after)))
    ;; Is (new)-data correct?
    (is (= (get-in data-after [id]) new-data))))


(deftest update-data-by-id-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 7)
        new-data {:x 1 :y 1}
        data-before (get-in @atom [:endpoints user endpoint :data])
        [{id :id} error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after (get-in @atom [:endpoints user endpoint :data])]
    ;; Action successful?
    (is (and id (not error)))
    ;; Is data still there?
    (is (= 3 (count data-after)))
    ;; Is data different?
    (is (not (= data-before data-after)))
    ;; Is (new)-data correct?
    (is (= (get-in data-after [(keyword id)]) new-data))))

(deftest update-data-by-id-non-existent-id
  (let [atom (atom storage-single-entry)
        user (first ids)
        endpoint (first endpoint-names)
        id (keyword (System/currentTimeMillis))
        new-data {:x 1 :y 1}
        data-before (get-in @atom [:endpoints user endpoint :data])
        [data  error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after (get-in @atom [:endpoints user endpoint :data])]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (= data-before data-after))))

(deftest update-data-by-id-non-existent-user
  (let [atom (atom storage-single-entry)
        user (keyword (System/currentTimeMillis))
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data {:x 1 :y 1}
        data-before @atom
        [data error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Is data different?
    (is (= data-before data-after))))

(deftest update-data-by-id-empty-storage
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data {:x 1 :y 1}
        data-before @atom
        [data error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest update-data-by-id-no-endpoints
  (let [atom (atom storage-empty)
        user (first ids)
        endpoint (first endpoint-names)
        id (nth ids 3)
        new-data {:x 1 :y 1}
        data-before @atom
        [data error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and error (not data)))
    ;; Still empty?
    (is (= data-before data-after))))

