(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [app.api :as api]))

(defsc Person [this {:person/keys [id name surname]}
               {:keys [checked? containing-list-id]}]
  {:query [:person/id :person/name
           :person/surname :person/age]
   :ident :person/id}
  (let [on-change #(comp/transact! this [(api/toggle {:list-id containing-list-id
                                                      :person-id id})])]
    (dom/li
     (dom/input {:type "checkbox"
                 :checked (true? checked?)
                 :onChange on-change})
     (dom/label (str name " " surname)))))

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [id people] :as params}]
  {:query [:list/id {:list/people (comp/get-query Person)}
           :ui/checked-set]
   :ident :list/id}
  (let [checked-set (:ui/checked-set params)
        total-number (count people)
        checked-number (count checked-set)
        all-checked? (and (< 0 total-number)
                          (= checked-number total-number))
        uncheck-all #(comp/transact! this
                                     [(api/uncheck-all {:list-id id})])
        check-all #(comp/transact! this
                                   [(api/check-all {:list-id id})])
        person->react-elem (fn [person] (ui-person (comp/computed person {:containing-list-id id
                                                                          :checked? (contains? checked-set (:person/id person))})))
        on-delete #(comp/transact! this [(api/delete-checked-items-from-list {:list-id id})])]
    (dom/div
     (dom/div
      (dom/span (str checked-number " Selected"))
      (if (< 0 checked-number)
        (dom/button {:onClick on-delete} "Delete")
        nil))
     (dom/ul
      (dom/li
       (dom/input {:type "checkbox"
                   :checked all-checked?
                   :onChange (if all-checked? uncheck-all check-all)})
       (dom/label "select all"))
      (mapv person->react-elem people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [_ data]
  {:query [{:root/buddies (comp/get-query PersonList)}
           {:root/foes (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div
   (ui-person-list (:root/buddies data))
   (ui-person-list (:root/foes data))))


