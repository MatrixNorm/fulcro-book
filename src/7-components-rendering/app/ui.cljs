(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [id name surname age]}]
  {:query [:person/id :person/name
             :person/surname :person/age]
   :ident :person/id}
  )

(defsc CheckboxItem [this {:item/keys [label]}]
  (dom/li
   (dom/input {:type "checkbox"})
   (dom/label label)))

(def ui-checkbox-item (comp/factory CheckboxItem
                                    {:keyfn :item/label}))

(defsc CheckboxList [this {:list/keys [items]}]
  (dom/ul
   (mapv ui-checkbox-item items)))

(def ui-checkbox-list (comp/factory CheckboxList))

(defsc Root [this props]
  {:query [{:root/degens []}]}
  (dom/div
   (ui-checkbox-list
    {:list/items [{:item/label "Joe Brandon"}
                  {:item/label "Dotard Dump"}
                  {:item/label "Banach Obongo"}
                  {:item/label "Shilary Kliptor"}
                  {:item/label "Crack Hunter"}]})))


