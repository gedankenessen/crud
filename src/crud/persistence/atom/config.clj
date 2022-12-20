(ns crud.persistence.atom.config)

(def config
  (atom {}))

(comment
  ;; Example structure of `storage` (without timestamp etc.)
  {:users
   {:63691793518fa064ce036c0c
    {:name "Foo"
     :email "foo@bar.org"
     :password "bcrypt+sha512$ba390f6f02f9db"
     :salt "[B@6401fcad]"
     :membership :free
     :status :unconfirmed}}
   :endpoints
   {:63691793518fa064ce036c0c
    {:focus
     {:methods []
      :userId 63691793518fa064ce036c0c
      :timestamp 1671548395158
      :data {:636a75a36a263c5cff4da190 {:x 1 :y 0}}}}}})

