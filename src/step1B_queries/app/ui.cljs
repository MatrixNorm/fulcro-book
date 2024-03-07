(ns app.ui
  (:require
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
                      (comp/get-initial-state Person {:name "Donald" :age 79})])})}

  (let [delete-person-cb (fn [name] (println label "asked to delete" name))
        make-person-elem
        (fn [person] (ui-person (comp/computed person
                                               {:onDelete delete-person-cb})))]
    (dom/div
     (dom/h4 label)
     (dom/ul (map make-person-elem people)))))

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

;; [{:friends (comp/get-query PersonList)}
;;  {:enemies (comp/get-query PersonList)}]

;; [{:friends [:list/label {:list/people (comp/get-query Person)}]}
;;  {:enemies [:list/label {:list/people (comp/get-query Person)}]}]

;; [{:friends [:list/label {:list/people [:person/name :person/age]}]}
;;  {:enemies [:list/label {:list/people [:person/name :person/age]}]}]

(comment)