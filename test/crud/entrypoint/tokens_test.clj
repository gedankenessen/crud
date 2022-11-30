(ns crud.tokens-test
  (:require [crud.entrypoint.tokens :as tokens]
            [clojure.test :refer [deftest is]]))

(def testConfig {:secret "testing" :version 1337})
(def testUserId "637f5eb6aff995fc95105fbd")

(deftest sign-valid-token
  (let [[{token :token} error] (tokens/sign-token testUserId testConfig)]
    (is (string? token))
    (is (nil? error))))

(deftest unsign-valid-token
  (let [[{raw :token} _] (tokens/sign-token testUserId testConfig)
        [token error] (tokens/unsign-token raw testConfig)]
    (is token)
    (is (contains? token :userId))
    (is (contains? token :exp))
    (is (contains? token :iat))
    (is (contains? token :ver))
    (is (= (:userId token) testUserId))
    (is (> (:exp token) (System/currentTimeMillis)))))

(deftest unsign-invalid-token
  (let [[token error] (tokens/unsign-token "this is definitly invalid bro" testConfig)]
    (is (nil? token))
    (is error)))

(deftest multi-roundtrip-token
  (let [[{token :token} _] (tokens/sign-token testUserId testConfig)
        [{userId :userId} _] (tokens/unsign-token token testConfig)
        [{token :token} _] (tokens/sign-token userId testConfig)
        [token error] (tokens/unsign-token token testConfig)]
    (is (nil? error))
    (is token)
    (is (= (:userId token) testUserId))))
