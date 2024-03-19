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
  (let [total-number (count items)
        checked-number (count (filter :ui/checked? items))
        ;; `checked-number` is calculated whenever any checkbox is
        ;; checked. Could this be a problem when `items` list is large?
        all-checked? (and (< 0 total-number)
                          (= checked-number total-number))
        uncheck-all #(comp/transact! this
                                     [(api/uncheck-all {:list-id :root/buddies})])
        check-all #(comp/transact! this
                                   [(api/check-all {:list-id :root/buddies})])
        on-change (if all-checked? uncheck-all check-all)]
    (dom/div
     (dom/div
      (dom/span (str checked-number " Selected"))
      (if (< 0 checked-number)
        (dom/button {:onClick #(println "delete buddy")} "Delete")
        nil))
     (dom/ul
      (dom/li
       (dom/input {:type "checkbox"
                   :checked all-checked?
                   :onChange on-change})
       (dom/label "select all"))
      (mapv ui-person items)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [_ data]
  {:query [{:root/buddies (comp/get-query Person)}]
   :initial-state {}}
  (dom/div
   (ui-person-list {:list/items (:root/buddies data)})))


