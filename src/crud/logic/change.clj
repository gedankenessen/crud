(ns crud.logic.change
  (:require [clojure.data :refer :all]))

(defn remove-nil-values [coll]
  (reduce-kv
   (fn [m k v]
     ;; Remove value if :nil
     (if (nil? v)
       (dissoc m k)
       (if (map? v)
         ;; Recursively call if map
         (let [res (remove-nil-values v)]
           (if (empty? res)
             ;; Remove entire k if map empty
             (dissoc m k)
             (assoc m k res)))
         (assoc m k v))))
   {}
   coll))

(defn nil-values [coll]
  (reduce-kv
   (fn [m k v]
     (assoc m k
            (if (map? v)
              (nil-values v)
              nil)))
   {} coll))

(defn has-changed? [a b]
  (let [x (nil-values (remove-nil-values a))
        y (nil-values (remove-nil-values b))
        [a b _] (diff x y)]
    (not
     (and
      (nil? a)
      (nil? b)))))
