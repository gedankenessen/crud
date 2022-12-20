(ns crud.persistence.atom.crud-test
  (:require
   [clojure.test :refer [deftest is]]
   [crud.persistence.atom.setups :refer :all]
   [crud.persistence.atom.crud :as atom]))

(deftest get-data-single-entry
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        expected [[{:id "637f5ea5470af2df05c6de4e" :x 0 :y 0}] nil]]
    (is (= expected (atom/get-data atom user endpoint)))))

(deftest get-data-multi-entry
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        expected [[{:id "637f5ea11bf698a0c296a877" :x 1 :y 2}
                   {:id "637f5ea5470af2df05c6de4e" :x 0 :y 0}
                   {:id "637f5ea8333408d8b57a8f71" :x 512 :y 124}]nil]]
    (is (= expected (atom/get-data atom user endpoint)))))

(deftest get-data-empty
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        expected [nil {}]
        result (atom/get-data atom user endpoint)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        expected [nil {}]
        result (atom/get-data atom user endpoint)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-no-user
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce01230c"
        endpoint "numbers"
        expected [nil {}]
        result (atom/get-data atom user endpoint)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-id-success
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea11bf698a0c296a877"
        expected [{:id "637f5ea11bf698a0c296a877" :x 1 :y 2} nil]
        result (atom/get-data-by-id atom user endpoint id)]
    (is (= expected result))))

(deftest get-data-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        expected [{:id "637f5ea5470af2df05c6de4e" :x 0 :y 0} nil]
        result (atom/get-data-by-id atom user endpoint id)]
    (is (= expected result))))

(deftest get-data-id-404-wrong-id
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5e636299384d332bd9e4"
        result (atom/get-data-by-id atom user endpoint id)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-id-404-no-endpoint
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5e636299384d332bd9e4"
        result (atom/get-data-by-id atom user endpoint id)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-id-404-no-user
  (let [atom (atom storage-no-endpoints)
        user "636917935181239871236c0c"
        endpoint "numbers"
        id "637f5e636299384d332bd9e4"
        result (atom/get-data-by-id atom user endpoint id)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-last-success
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        expected [{:id "637f5ea11bf698a0c296a877" :x 1 :y 2} nil]
        result (atom/get-data-last atom user endpoint)]
    (is (= expected result))))

(deftest get-data-last-success-single
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        expected [{:id "637f5ea5470af2df05c6de4e" :x 0 :y 0} nil]
        result (atom/get-data-last atom user endpoint)]
    (is (= expected result))))

(deftest get-data-last-no-data
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        result (atom/get-data-last atom user endpoint)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-last-no-endpoint
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        result (atom/get-data-last atom user endpoint)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest get-data-last-empty
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        result (atom/get-data-last atom user endpoint)]
    (is (nil? (first result)))
    (is (not (nil? (second result))))
    (is (= 404 (:status (second result))))))

(deftest add-endpoint-success
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        data {:x 0 :y 0}
        [response error] (atom/add-endpoint atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is endpoint now there?
    (is (contains? (get @atom (keyword user)) (keyword endpoint)))
    ;; Does key exist?
    (is (contains? (get-in @atom [(keyword user) (keyword endpoint) :data]) (keyword id)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Is something in data
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-endpoint-success-multiple-present
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "test"
        data {:message "Testing the endpoint"}
        [response error] (atom/add-endpoint atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is endpoint now there?
    (is (contains? (get @atom (keyword user)) (keyword endpoint)))
    ;; Does key exist?
    (is (contains? (get-in @atom [(keyword user) (keyword endpoint) :data]) (keyword id)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Is something in data
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-endpoint-storage-empty
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "test"
        data {:message "Testing the endpoint"}
        [response error] (atom/add-endpoint atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is endpoint now there?
    (is (contains? (get @atom (keyword user)) (keyword endpoint)))
    ;; Does key exist?
    (is (contains? (get-in @atom [(keyword user) (keyword endpoint) :data]) (keyword id)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Is something in data
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-endpoint-already-exists
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        data {:message "Testing the endpoint"}
        [response error] (atom/add-endpoint atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is endpoint now there?
    (is (contains? (get @atom (keyword user)) (keyword endpoint)))
    ;; Does key exist?
    (is (contains? (get-in @atom [(keyword user) (keyword endpoint) :data]) (keyword id)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Is something in data
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-data-success-single-entry
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        data {:x 12 :y 12}
        [response error] (atom/add-data atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is endpoint now there?
    (is (contains? (get @atom (keyword user)) (keyword endpoint)))
    ;; Does key exist?
    (is (contains? (get-in @atom [(keyword user) (keyword endpoint) :data]) (keyword id)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Is something in data
    (is (= 2 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-data-sucess-multi-entry
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        data {:x 701 :y 401}
        [response error] (atom/add-data atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is endpoint now there?
    (is (contains? (get @atom (keyword user)) (keyword endpoint)))
    ;; Does key exist?
    (is (contains? (get-in @atom [(keyword user) (keyword endpoint) :data]) (keyword id)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Is something in data
    (is (= 4 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-data-empty-storage
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c15"
        endpoint "numbers"
        data {:x 701 :y 401}
        [response error] (atom/add-data atom user endpoint data)]
    ;; Action unsuccessful?
    (is (and (not response) error))
    ;; Storage still empty?
    (is (= @atom {}))))

(deftest add-data-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c15"
        endpoint "numbers"
        data {:x 701 :y 401}
        data-before @atom
        [response error] (atom/add-data atom user endpoint data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and (not response) error))
    ;; Storage still empty?
    (is (= data-before data-after))))

(deftest add-version-single-entry
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        data {:foo "bar"}
        [response error] (atom/add-version atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Did old-data get replaced by data?
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-version-multi-entry
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        data {:foo "bar"}
        [response error] (atom/add-version atom user endpoint data)
        id (str (:id response))]
    ;; Action successful?
    (is (and (not (nil? data)) (nil? error)))
    ;; Id not nil?
    (is (not (nil? id )))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is other endpoint still there?
    (is (= 2 (count (get-in @atom [(keyword user)]))))
    ;; Is data in correct format?
    (is (= data (get-in @atom [(keyword user) (keyword endpoint) :data (keyword id)])))
    ;; Did old-data get replaced by data?
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest add-version-empty-storage
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c15"
        endpoint "numbers"
        data {:x 701 :y 401}
        [response error] (atom/add-version atom user endpoint data)]
    ;; Action unsuccessful?
    (is (and (not response) error))
    ;; Storage still empty?
    (is (= @atom {}))))

(deftest add-version-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c15"
        endpoint "numbers"
        data {:x 701 :y 401}
        data-before @atom
        [response error] (atom/add-version atom user endpoint data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and (not response) error))
    ;; Storage still empty?
    (is (= data-before data-after))))

(deftest delete-data-by-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        [response error] (atom/delete-data-by-id atom user endpoint id)]
    ;; Action successful?
    (is (and (not (nil? response)) (nil? error)))
    ;; Id correct?
    (is (= (str (:id response)) id))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is entry deleted?
    (is (empty? (get-in @atom [(keyword user) (keyword endpoint) :data])))))

(deftest delete-data-by-id-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea8333408d8b57a8f71"
        [response error] (atom/delete-data-by-id atom user endpoint id)]
    ;; Action successful?
    (is (and (not (nil? response)) (nil? error)))
    ;; Id correct?
    (is (= (str (:id response)) id))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is entry deleted?
    (is (= 2 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest delete-data-by-id-non-existent-id
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de15"
        [response error] (atom/delete-data-by-id atom user endpoint id)]
    ;; Action successful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is data still there?
    (is (= 1 (count (get-in @atom [(keyword user) (keyword endpoint) :data]))))))

(deftest delete-data-by-id-non-existent-user
  (let [atom (atom storage-single-entry)
        real-user "63691793518fa064ce036c0c"
        fake-user "63691793518fa064ce036c15"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        [response error] (atom/delete-data-by-id atom fake-user endpoint id)]
    ;; Action successful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is user still there?
    (is (contains? @atom (keyword real-user)))
    ;; Is data still there?
    (is (= 1 (count (get-in @atom [(keyword real-user) (keyword endpoint) :data]))))))

(deftest delete-data-by-id-non-existent-endpoint
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        real-endpoint "numbers"
        fake-endpoint "xyz"
        id "637f5ea5470af2df05c6de4e"
        [response error] (atom/delete-data-by-id atom user fake-endpoint id)]
    ;; Action successful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is user still there?
    (is (contains? @atom (keyword user)))
    ;; Is data still there?
    (is (= 1 (count (get-in @atom [(keyword user) (keyword real-endpoint) :data]))))))

(deftest delete-data-by-id-empty-storage
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        [response error] (atom/delete-data-by-id atom user endpoint id)]
    ;; Action unsuccessful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Still empty?
    (is (= {} @atom))))

(deftest delete-data-by-id-no-endpoints
  (let [atom (atom storage-no-endpoints)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        data-before @atom
        [response error] (atom/delete-data-by-id atom user endpoint id)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Still empty?
    (is (= data-before data-after))))

(deftest update-data-by-id-success-single-entry
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        new-data {:x 1 :y 1}
        data-before (get-in @atom [(keyword user) (keyword endpoint) :data])
        [response error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after (get-in @atom [(keyword user) (keyword endpoint) :data])]
    ;; Action successful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (not (= data-before data-after)))
    ;; Is (new)-data correct?
    (is (= (get-in data-after [(keyword id)]) new-data))))

(deftest update-data-by-id-success-multi-entry
  (let [atom (atom storage-multi-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea8333408d8b57a8f71"
        new-data {:x 1 :y 1}
        data-before (get-in @atom [(keyword user) (keyword endpoint) :data])
        [response error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after (get-in @atom [(keyword user) (keyword endpoint) :data])]
    ;; Action successful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is data still there?
    (is (= 3 (count data-after)))
    ;; Is data different?
    (is (not (= data-before data-after)))
    ;; Is (new)-data correct?
    (is (= (get-in data-after [(keyword id)]) new-data))))

(deftest update-data-by-id-non-existent-id
  (let [atom (atom storage-single-entry)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea8333408d8b57a8f71"
        new-data {:x 1 :y 1}
        data-before (get-in @atom [(keyword user) (keyword endpoint) :data])
        [response error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after (get-in @atom [(keyword user) (keyword endpoint) :data])]
    ;; Action unsuccessful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (= data-before data-after))))

(deftest update-data-by-id-non-existent-user
  (let [atom (atom storage-single-entry)
        real-user "63691793518fa064ce036c0c"
        fake-user "63691793518fa064ce036c15"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        new-data {:x 1 :y 1}
        data-before (get-in @atom [(keyword real-user) (keyword endpoint) :data])
        [response error] (atom/update-data-by-id atom fake-user endpoint id new-data)
        data-after (get-in @atom [(keyword real-user) (keyword endpoint) :data])]
    ;; Action unsuccessful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Is data still there?
    (is (= 1 (count data-after)))
    ;; Is data different?
    (is (= data-before data-after))))

(deftest update-data-by-id-empty-storage
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        new-data {:x 1 :y 1}
        [response error] (atom/update-data-by-id atom user endpoint id new-data)]
    ;; Action unsuccessful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Still empty?
    (is (= {} @atom))))

(deftest update-data-by-id-no-endpoints
  (let [atom (atom storage-empty)
        user "63691793518fa064ce036c0c"
        endpoint "numbers"
        id "637f5ea5470af2df05c6de4e"
        new-data {:x 1 :y 1}
        data-before @atom
        [response error] (atom/update-data-by-id atom user endpoint id new-data)
        data-after @atom]
    ;; Action unsuccessful?
    (is (and (not (nil? error)) (nil? response)))
    ;; Still empty?
    (is (= data-before data-after))))
