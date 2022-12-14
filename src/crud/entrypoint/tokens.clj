(ns crud.entrypoint.tokens
  (:require [buddy.sign.jwt :as jwt]
            [crud.utility :refer [build-config]]))

(def config
  {:secret "secret"
   :version 0})

(def relevent-keys [:secret :version])

(defn get-args-config
  [{secret :crud-token-secret
    version :crud-token-version}]
  {:secret secret :version version})

(defn get-env-config []
  {:secret (System/getenv "CRUD_TOKEN_SECRET")
   :version (System/getenv "CRUD_TOKEN_VERSION")})

(def get-cli-options
  [["-s" "--crud-token-secret SECRET" "Secret used for tokens"
    :parse-fn #(str %)]
   ["-S" "--crud-token-version version" "Version used for tokens"
    :parse-fn #(Integer/parseInt %)]])

(defn get-config [args]
  (build-config
   relevent-keys
   config
   (get-env-config)
   (get-args-config args)))

;; TODO: Wrap with try-catch!
;; TODO: Defaults to hs256, is this good enough?
(defn sign-token
  "Return a token from a `userId` and `config`"
  ([userId]
   (sign-token userId config))
  ([userId {version :version secret :secret}]
   (if (not userId)
     [nil {:message "Something went wrong" :status 500}]
     [{:token (jwt/sign
               (let [now (System/currentTimeMillis)]
                 {:userId (name userId)
                  :ver version
                  :iat now
                  ;; Expiration date: today + 30days
                  :exp (+ (* 1000 60 60 24 30) now)})
               secret)}
      nil])))

(comment
  ;; Both string and keyword should work
  ;; (Both should result in a userId that is a string)
  (sign-token "63691793518fa064ce036c0c" config)
  (sign-token :63691793518fa064ce036c0c config))

(defn unsign-token
  "Unwrap token to `userId` and other metadata"
  ([token]
   (unsign-token token config))
  ([token {secret :secret}]
   (try
     (if (or (nil? token) (empty? token))
       [nil {:message "Authorization token is missing or malformed" :status 400}]
       (let [token (jwt/unsign token secret)]
         (cond
           ;; Case: token or userId is null
           (nil? (:userId token)) [nil {:message "Malformed token" :status 403}]
           ;; exp is null?
           (nil? (:exp token)) [nil {:message "Malformed token" :status 403}]
           ;; token is expired?
           (< (:exp token) (System/currentTimeMillis)) [nil {:message "Expired token" :status 403}]
           ;; token should be valid -> make sure to parse into keyword again!
           :else [(update-in token [:userId] keyword) nil])))
     (catch Exception _
       [nil {:message "Malformed token" :status 403}]))))

(comment
  (let [config {:secret "testing" :version 1337}
        userId :63691793518fa064ce036c0c
        [{token :token} _] (sign-token userId config)
        data (unsign-token token config)]
    data))
