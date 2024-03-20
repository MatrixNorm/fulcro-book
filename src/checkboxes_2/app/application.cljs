(ns app.application
  (:require
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro.algorithms.normalize :as fnorm]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]))

(defonce APP (with-react18 (app/fulcro-app)))

(comment
  (app/current-state APP)

  (def data-tree
    {:root/people
     [{:person/id 1 :person/name "Joe"
       :person/surname "Brandon"}
      {:person/id 2 :person/name "Dotard"
       :person/surname "Dump"}
      {:person/id 3 :person/name "Banach"
       :person/surname "Obongo"}
      {:person/id 4 :person/name "Shilary"
       :person/surname "Kliptor"}
      {:person/id 5 :person/name "Crack"
       :person/surname "Hunter"}]})

  (merge/merge! APP data-tree
                (comp/get-query app.ui/Root))

  (comp/get-query app.ui/Root)
  (fnorm/tree->db app.ui/Root data-tree true)

  (merge/merge-component! APP app.ui/Person
                          {:person/id 2
                           :person/name "Dotard"
                           :person/surname "Dump"})
  ;;
  )