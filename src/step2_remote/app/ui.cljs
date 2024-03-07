(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [app.mutations :as api]))

(defsc Person [this
               {:person/keys [id name age]}
               {:keys [onDelete]}]
  {:query [:person/id :person/name :person/age]
   :ident :person/id}
  (dom/li
   (dom/h5 (str name " (age: " age ")"))
   (dom/button {:onClick #(onDelete id)} "X")))

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [id people]}]
  {:query [:list/id {:list/people (comp/get-query Person)}]
   :ident :list/id}

  (let [delete-person-cb
        (fn [person-id] (comp/transact! this [(api/delete-person
                                               {:list/id id
                                                :person/id person-id})]))
        person->react-elem
        (fn [person] (ui-person (comp/computed person
                                               {:onDelete delete-person-cb})))]
    (dom/div
     (dom/ul
      (map person->react-elem people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div
   (dom/h3 "Friends")
   (when friends
     (ui-person-list friends))
   (dom/h3 "Enemies")
   (when enemies
     (ui-person-list enemies))))

;; (comment
;;   (def x (comp/get-initial-state Root {}))
;;   (:friends x))

;; (comment
;;   (def delete-person (fn [name] (println "asked to delete" name)))
;;   (comp/computed {:name "Bobby" :age 55} {:onDelete delete-person}))
