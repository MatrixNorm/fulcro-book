(ns app.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-person
  "Delete the person with `:person/id` from the list with `:list/id`"
  [{list-id :list/id person-id :person/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident*
                 [:person/id person-id] [:list/id list-id :list/people])))


(comment
  (def state (com.fulcrologic.fulcro.application/current-state
              app.application/APP))
  (def query (com.fulcrologic.fulcro.components/get-query
              app.ui/Root))
  (com.fulcrologic.fulcro.algorithms.denormalize/db->tree
   query state state)
  ;;
  )
