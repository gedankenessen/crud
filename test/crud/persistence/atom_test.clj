(ns crud.atom-test
  (:require [crud.persistence.atom :as atom]
            [clojure.test :refer [deftest is]]))

;; Prepared data to make testing easier

(def storage-empty {})

(def storage-no-endpoints
  {:63691793518fa064ce036c0c {}})

(def storage-no-data
  {:63691793518fa064ce036c0c
   {:numbers
    {:id "637f5eb6aff995fc95105fbd"
     :timestamp 1669291723903
     :data {}}}})

(def storage-single-entry
  {:63691793518fa064ce036c0c
   {:numbers
    {:id "637f5eb6aff995fc95105fbd"
     :timestamp 1669291723903
     :data
     {:637f5ea5470af2df05c6de4e {:x 0 :y 0}}}}})

(def storage-multi-entry
  {:63691793518fa064ce036c0c
   {:names
    {:timestamp 1669291439706
     :data
     {:637f5e636299384d332bd9e4 {:name "Janet"}
      :637f5e7e38f0162c0da10a8c {:name "Eric"}
      :637f5e8734fc6b41c6be9592 {:name "Tom"}}}
    :numbers
    {:id "637f5eb6aff995fc95105fbd"
     :timestamp 1669291723903
     :data
     {:637f5ea11bf698a0c296a877 {:x 1 :y 2}
      :637f5ea5470af2df05c6de4e {:x 0 :y 0}
      :637f5ea8333408d8b57a8f71 {:x 512 :y 124}}}}})

;; Actual tests start here

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
    ;; Action susccessful?
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
    ;; Action susccessful?
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
    ;; Action susccessful?
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
    ;; Action susccessful?
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

;; TODO: Tests add-data
;; TODO: Tests add-version
;; TODO: Tests delete-data-by-id
;; TODO: Tests update-data-by-id
