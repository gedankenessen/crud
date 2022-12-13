(ns crud.utility)

(defn remove-nils [map]
  (into {} (filter second map)))

(defn build-config [keys defaults envs args]
  (select-keys
   (merge
    defaults
    (remove-nils envs)
    (remove-nils args))
   keys))
