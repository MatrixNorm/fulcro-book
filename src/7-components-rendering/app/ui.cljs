(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [name surname]}]
  {:query [:person/id :person/name
           :person/surname :person/age]
   :ident :person/id}
  (dom/li
   (dom/input {:type "checkbox"})
   (dom/label (str name " " surname))))

(def ui-person (comp/factory Person
                             {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [items]}]
  (dom/ul
   (dom/li
    (dom/input {:type "checkbox"})
    (dom/label "select all"))
   (mapv ui-person items)))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this data]
  {:query [{:root/people (comp/get-query Person)}]}
  (dom/div
   (ui-person-list {:list/items (:root/people data)})))


