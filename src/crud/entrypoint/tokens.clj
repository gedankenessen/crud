(ns crud.entrypoint.tokens
  (:require [buddy.sign.jwt :as jwt]))

;; TODO: Read-in from env-var
(def config
  {:secret "secret"
   :version 0})

;; TODO: Wrap with try-catch!
;; TODO: Defaults to hs256, is this good enough?
(defn sign-token
  "Return a token from a `userId` and `config`"
  ([userId]
   (sign-token userId config))
  ([userId {version :version secret :secret}]
   (if (or (not userId)
           (empty? userId))
     [nil {:message "Something went wrong" :status 500}]
     [{:token (jwt/sign
               (let [now (System/currentTimeMillis)]
                 {:userId userId
                  :ver version
                  :iat now
                  ;; Expiration date: today + 30days
                  :exp (+ (* 1000 60 60 24 30) now)})
               secret)}
      nil])))

(comment
  (sign-token "63691793518fa064ce036c0c" config))

(defn unsign-token
  "Unwrap token to `userId` and other metadata"
  ([token]
   (unsign-token token config))
  ([token {secret :secret}]
   (try
     (if (or (nil? token) (empty? token))
       [nil {:message "Authorization token is missing" :status 401}]
       (let [token (jwt/unsign token secret)]
         (cond
           ;; Case: token or userId is null
           (nil? (:userId token)) [nil {:message "Malformed token" :status 403}]
           ;; exp is null?
           (nil? (:exp token)) [nil {:message "Malformed token" :status 403}]
           ;; token is expired?
           (< (:exp token) (System/currentTimeMillis)) [nil {:message "Expired token" :status 403}]
           ;; token should be valid
           :else [token nil])))
     (catch Exception _
       [nil {:message "Malformed token" :status 403}]))))

(comment
  (let [config {:secret "testing" :version 1337}
        userId "63691793518fa064ce036c0c"
        [{token :token} _] (sign-token userId config)
        data (unsign-token token config)]
    data))
