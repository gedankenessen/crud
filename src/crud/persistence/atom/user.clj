(ns crud.persistence.atom.user)

(defn get-user-by-email [atom email]
  [nil {:message (str "Could not find user with email " email) :status 404}])

(defn get-user-by-id [atom id]
  [nil {:message (str "Could not find user with id " id) :status 404}])

(defn add-user [atom data]
  [nil {:message "Could not add user" :status 500}])

(defn update-user [atom id data]
  [nil {:message "Could not update user" :status 500}])

(defn delete-user [atom id data]
  [nil {:message "Could not delete user" :status 500}])
