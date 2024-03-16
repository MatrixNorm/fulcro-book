(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as m]
   [app.api :as api]))

(defsc Person [this {:person/keys [name surname]
                     :ui/keys [checked?]}]
  {:query [:person/id :person/name
           :person/surname :person/age :ui/checked?]
   :ident :person/id}
  (dom/li
   (dom/input {:type "checkbox"
               :checked (true? checked?)
               :onChange #(m/toggle! this :ui/checked?)})
   (dom/label (str name " " surname))))

(def ui-person (comp/factory Person
                             {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [items]}]
  (let [all-checked? (if (empty? items)
                       false
                       (every? :ui/checked? items))]
    (dom/ul
     (dom/li
      (dom/input {:type "checkbox"
                  :checked all-checked?
                  :onChange #(if all-checked?
                               (comp/transact! this [(api/uncheck-all {:list-id :root/people})])
                               (comp/transact! this [(api/check-all {:list-id :root/people})]))})
      (dom/label "select all"))
     (mapv ui-person items))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [_ data]
  {:query [{:root/people (comp/get-query Person)}]
   :initial-state {}}
  (dom/div
   (ui-person-list {:list/items (:root/people data)})))


