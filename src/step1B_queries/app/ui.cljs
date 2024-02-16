(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [name age]}]
  {:initial-state
   (fn [{:keys [name age]}] {:person/name name :person/age age})}
  (dom/li
   (dom/h5 (str name " (age: " age ")"))))

(def ui-person (comp/factory Person))


(defsc PersonList [this {:list/keys [label people]}]
  {:initial-state
   (fn [{:keys [label]}]
     {:list/label  label
      :list/people (if (= label "Friends")
                     [(comp/get-initial-state Person {:name "Sally" :age 32})
                      (comp/get-initial-state Person {:name "Brandon" :age 81})]
                     [(comp/get-initial-state Person {:name "Fred" :age 11})
                      (comp/get-initial-state Person {:name "Bobby" :age 55})])})}
  (dom/div
   (dom/h4 label)
   (dom/ul
    (map ui-person people))))

(def ui-person-list (comp/factory PersonList))


(defsc Root [this {:keys [friends enemies]}]
  {:initial-state
   (fn [params] {:friends (comp/get-initial-state PersonList {:label "Friends"})
                 :enemies (comp/get-initial-state PersonList {:label "Enemies"})})}
  (dom/div
   (ui-person-list friends)
   (ui-person-list enemies)))


(comment
  (def initial-state 
    (comp/get-initial-state app.ui/Root {}))
  
  (com.fulcrologic.fulcro.application/current-state app.application/APP)

  (com.fulcrologic.fulcro.algorithms.denormalize/db->tree 
   [{:friends [:list/label]}] initial-state {})
  )