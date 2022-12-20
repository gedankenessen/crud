(ns crud.persistence.atom.setups)

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
    {:id "64115ebaf3f995fc9512ffad"
     :timestamp 1669291439706
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
