(ns crud.persistence.atom.utility)

(defn fresh-uuid! []
  (keyword (str (java.util.UUID/randomUUID))))

(defn fresh-timestamp! []
  (System/currentTimeMillis))


