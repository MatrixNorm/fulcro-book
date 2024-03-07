(ns app.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

;; The mutation code is tied to the shape of the UI tree!!!
;; This breaks our lovely model in several ways:
;; 1. We can’t refactor our UI without also rewriting the mutations 
;;    (since the data tree would change shape)
;; 2. We can’t locally reason about any data. 
;;    Our mutations have to understand things globally!
;; 3. Our mutations could get rather large and ugly as our UI gets big
;; 4. If a fact appears in more than one place in the UI and data
;;    tree, then we’ll have to update all of them in order for
;;    things to be correct. Data duplication is never your friend.

(defn __delete-person [state-val list-name person-name]
  (let [path (if (= "Friends" list-name)
               [:friends :list/people]
               [:enemies :list/people])
        old-list (get-in state-val path)
        new-list (vec (filter #(not= (:person/name %) person-name) old-list))]
    #(assoc-in % path new-list)))

(defmutation delete-person
  "Mutation: Delete the person with `name` from the list with `list-name`"
  [{:keys [list-name name]}]
  (action [{:keys [state]}]
          (let [next-state-fn (__delete-person @state list-name name)]
            (swap! state next-state-fn))))

(comment
  (def app-state
    (com.fulcrologic.fulcro.application/current-state
     app.application/APP))

  (def state {:friends
              {:list/label "Friends",
               :list/people [{:person/name "Sally", :person/age 32}
                             {:person/name "Brandon", :person/age 81}]},
              :enemies
              {:list/label "Enemies",
               :list/people [{:person/name "Fred", :person/age 11}
                             {:person/name "Bobby", :person/age 55}]}})

  (let [f (__delete-person state "Friends" "Brandon")]
    (f state))
  ;; The defmutation macro returns a function-like object 
  ;; that just returns itself as data when called "normally"
  (delete-person {:list-name "Brandon" :name "Joe"})
  ;;
  )