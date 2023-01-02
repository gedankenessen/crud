(ns integration.base
  (:require [clojure.test :refer [deftest is]]
            [crud.entrypoint.core :refer :all]
            [crud.config :refer [config get-config]]
            [ring.mock.request :as mock]
            [crud.entrypoint.tokens :as tokens]
            [clojure.data.json :as json]
            [crud.persistence.local.crud :as lcrud]))

(deftest register-login-post-get
  (let [db (atom {})
        handler (build-routes (get-config {:default-db db}))
        name "Bot"
        email "foo@bar.org"
        password "test123"
        endpoint "points"]
    (let [_ (handler (-> (mock/request :post "/user/register")
                         (mock/json-body {:email email
                                          :password password
                                          :name name})))
          token (:token
                 (json/read-json
                  (:body
                   (handler (-> (mock/request :post "/user/login")
                                (mock/json-body {:email email
                                                 :password password}))))))
          _ (handler (-> (mock/request :post "/build/points")
                         (mock/header "Authorization" token)
                         (mock/json-body {:x (Math/random) :y (Math/random) :z (Math/random)})))
          r (handler (-> (mock/request :get "/build/points")
                         (mock/header "Authorization" token)))
          res (json/read-str (:body r))]
      (is (= 1 (count res)))
      (is (and
           (contains? (first res) "x")
           (contains? (first res) "y")
           (contains? (first res) "z")
           (contains? (first res) "_id"))))))


(deftest register-login-post-post-get
  (let [db (atom {})
        handler (build-routes (get-config {:default-db db}))
        name "Bot"
        email "foo@bar.org"
        password "test123"
        endpoint "points"]
    (let [_ (handler (-> (mock/request :post "/user/register")
                         (mock/json-body {:email email
                                          :password password
                                          :name name})))
          token (:token
                 (json/read-json
                  (:body
                   (handler (-> (mock/request :post "/user/login")
                                (mock/json-body {:email email
                                                 :password password}))))))
          _ (handler (-> (mock/request :post "/build/points")
                         (mock/header "Authorization" token)
                         (mock/json-body {:x (Math/random) :y (Math/random) :z (Math/random)})))
          _ (handler (-> (mock/request :post "/build/points")
                         (mock/header "Authorization" token)
                         (mock/json-body {:x (Math/random) :y (Math/random) :z (Math/random)})))
          r (handler (-> (mock/request :get "/build/points")
                         (mock/header "Authorization" token)))
          res (json/read-str (:body r))]
      (is (= 2 (count res)))
      (is (every? #(and
                    (contains? % "x")
                    (contains? % "y")
                    (contains? % "z")
                    (contains? % "_id")) res)))))

(deftest register-login-post-change-get
  (let [db (atom {})
        handler (build-routes (get-config {:default-db db}))
        name "Bot"
        email "foo@bar.org"
        password "test123"
        endpoint "points"]
    (let [_ (handler (-> (mock/request :post "/user/register")
                         (mock/json-body {:email email
                                          :password password
                                          :name name})))
          token (:token
                 (json/read-json
                  (:body
                   (handler (-> (mock/request :post "/user/login")
                                (mock/json-body {:email email
                                                 :password password}))))))
          _ (handler (-> (mock/request :post "/build/points")
                         (mock/header "Authorization" token)
                         (mock/json-body {:x (Math/random) :y (Math/random) :z (Math/random)})))
          _ (handler (-> (mock/request :post "/build/points")
                         (mock/header "Authorization" token)
                         (mock/json-body {:word "Chimera" :lang :eng})))
          r (handler (-> (mock/request :get "/build/points")
                         (mock/header "Authorization" token)))
          res (json/read-str (:body r))]
      (is (= 1 (count res)))
      (is (and
           (contains? (first res) "word")
           (contains? (first res) "lang")
           (contains? (first res) "_id"))))))

(deftest register-login-post-post-other-get
  (let [db (atom {})
        handler (build-routes (get-config {:default-db db}))
        name "Bot"
        email "foo@bar.org"
        password "test123"
        endpoint "points"]
    (let [_ (handler (-> (mock/request :post "/user/register")
                         (mock/json-body {:email email
                                          :password password
                                          :name name})))
          token (:token
                 (json/read-json
                  (:body
                   (handler (-> (mock/request :post "/user/login")
                                (mock/json-body {:email email
                                                 :password password}))))))
          _ (handler (-> (mock/request :post "/build/points")
                         (mock/header "Authorization" token)
                         (mock/json-body {:x (Math/random) :y (Math/random) :z (Math/random)})))
          _ (handler (-> (mock/request :post "/build/words")
                         (mock/header "Authorization" token)
                         (mock/json-body {:word "Chimera" :lang :eng})))
          res-words (-> (handler (-> (mock/request :get "/build/words")
                                     (mock/header "Authorization" token)))
                        :body
                        (json/read-str))
          res-points (-> (handler (-> (mock/request :get "/build/points")
                                      (mock/header "Authorization" token)))
                         :body
                         (json/read-str))]
      (is (= 1 (count res-words)))
      (is (and
           (contains? (first res-words) "word")
           (contains? (first res-words) "lang")
           (contains? (first res-words) "_id")))
      (is (= 1 (count res-points)))
      (is (and
           (contains? (first res-points) "x")
           (contains? (first res-points) "y")
           (contains? (first res-points) "z")
           (contains? (first res-points) "_id"))))))
