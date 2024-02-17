(ns app.ui
  (:require
   [app.mutations :as api]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [name age]} {:keys [onDelete]}]
  {:query [:person/name :person/age]
   :initial-state
   (fn [{:keys [name age]}] {:person/name name :person/age age})}
  (dom/li
   (dom/h5 (str name " (age: " age ")")
           (dom/button {:onClick #(onDelete name)} "X"))))

(def ui-person (comp/factory Person {:keyfn :person/name}))


(defsc PersonList [this {:list/keys [label people]}]
  {:query [:list/label {:list/people (comp/get-query Person)}]
   :initial-state
   (fn [{:keys [label]}]
     {:list/label  label
      :list/people (if (= label "Friends")
                     [(comp/get-initial-state Person {:name "Sally" :age 32})
                      (comp/get-initial-state Person {:name "Brandon" :age 81})]
                     [(comp/get-initial-state Person {:name "Fred" :age 11})
                      (comp/get-initial-state Person {:name "Bobby" :age 55})])})}

  (let [delete-person-cb 
        (fn [name] (comp/transact! this 
                                   [(api/delete-person {:list-name label :name name})]))
        person-fn 
        (fn [person] (ui-person (comp/computed person 
                                               {:onDelete delete-person-cb})))]
    (dom/div
     (dom/h4 label)
     (dom/ul (map person-fn people)))))

(def ui-person-list (comp/factory PersonList))


(defsc Root [this {:keys [friends enemies]}]
  {:query [{:friends (comp/get-query PersonList)}
           {:enemies (comp/get-query PersonList)}]
   :initial-state
   (fn [params] {:friends (comp/get-initial-state PersonList {:label "Friends"})
                 :enemies (comp/get-initial-state PersonList {:label "Enemies"})})}
  (dom/div
   (ui-person-list friends)
   (ui-person-list enemies)))

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