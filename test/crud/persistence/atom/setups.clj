(ns crud.persistence.atom.setups)

(def demo-user-1
  {:name "Tmo"
   :email "tom@bar.org"
   :password "bcrypt+sha512$ba390f6f02f9db"
   :salt "[B@6401fcad]"
   :membership :free
   :status :unconfirmed})

(def demo-user-2
  {:name "Janet"
   :email "blub@blab.com"
   :password "bcrypt+sha512$1230974122f9db"
   :salt "[B@612031fa]"
   :membership :pro
   :status :unconfirmed})

(def demo-user-3
  {:name "Eric"
   :email "lambda@panda.de"
   :password "bcrypt+sha512$1ffabc2122f9db"
   :salt "[B@6f2031fa]"
   :membership :free
   :status :unconfirmed})

(def demo-data-1 {:x 0 :y 0})
(def demo-data-2 {:x 10 :y 11})
(def demo-data-3 {:x 701 :y 401})

(def demo-data-4 {:word "Spaghetti" :lang "German"})
(def demo-data-5 {:word "Rouge" :lang "French"})
(def demo-data-6 {:word "Table" :lang "English"})

(def endpoint-names [:numbers :words])

(def ids [:63691793518fa064ce036c0c
          :637f5e636299384d332bd9e4
          :637f5e7e38f0162c0da10a8c
          :637f5e8734fc6b41c6be9592
          :637f5e636299384d332bd9e4
          :63a1d33e33cd5145ce177df0
          :63a1d3439d1ad16795011d8e
          :63a1d34867eee51b1d57a8c0
          :63a1d34fddc21b998058bff6
          :63a1d3574027fa5e030e047c
          :63a1d89fb45bab6e38273585])

(def storage-empty {})

(def storage-no-endpoints
  {:users
   {(first ids) demo-user-1}
   :endpoints
   {(first ids) {}}})

(def storage-no-data
  {:users {(first ids) demo-user-1}
   :endpoints
   {(first ids)
    {(first endpoint-names)
     {:_id (second ids)
      :timestamp 1669291723903
      :data {}}}}})

(def storage-single-entry
  {:users {(first ids) demo-user-1}
   :endpoints
   {(first ids)
    {(first endpoint-names)
     {:_id (second ids)
      :timestamp 1669291723903
      :data
      {(nth ids 3) demo-data-1}}}}})

(def storage-multi-entry
  {:users {(first ids) demo-user-1}
   :endpoints
   {(first ids)
    {(second endpoint-names)
     {:_id (second ids)
      :timestamp 1669291439706
      :data
      {(nth ids 3) demo-data-4
       (nth ids 4) demo-data-5
       (nth ids 5) demo-data-6}}
     (first endpoint-names)
     {:_id (nth ids 6)
      :timestamp 1669291723903
      :data
      {(nth ids 7) demo-data-1
       (nth ids 8) demo-data-2
       (nth ids 9) demo-data-3}}}}})
