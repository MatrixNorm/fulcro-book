(ns app.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-person
  "Mutation: Delete the person with `:person/id` from the list with `:list/id`"
  [{list-id   :list/id
    person-id :person/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident*
                 [:person/id person-id]
                 [:list/id list-id :list/people]))
  ;; Remote will recieve (app.mutations/delete-person ...)
  ;; This must be equal to the value of ::pc/sym on the server side.
  ;; XXX is where a way to override default namespace for
  ;; the mutations symbol?
  (remote [env] true))
