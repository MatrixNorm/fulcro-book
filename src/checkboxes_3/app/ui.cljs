(ns app.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [app.api :as api]))


(defsc Person [this {:person/keys [id name surname]}
               {:keys [checked? onChange]}]
  {:query [:person/id :person/name
           :person/surname :person/age]
   :ident :person/id}
  (dom/li
   (dom/input {:type "checkbox"
               :checked (true? checked?)
               :onChange onChange})
   (dom/label (str name " " surname))))

(def ui-person (comp/factory Person {:keyfn :person/id}))


(defsc Cake [this {:cake/keys [id name country]}
             {:keys [checked? onChange]}]
  {:query [:cake/id :cake/name :cake/country]
   :ident :cake/id}
  (dom/li
   (dom/input {:type "checkbox"
               :checked (true? checked?)
               :onChange onChange})
   (dom/label (str name " (" country ")"))))

(def ui-cake (comp/factory Cake {:keyfn :cake/id}))


(defn ListComponent [{:keys [this list-id items checked-set item-id-key item-element]}]
  (let [total-number (count items)
        checked-number (count checked-set)
        all-checked? (and (< 0 total-number)
                          (= checked-number total-number))
        uncheck-all
        #(comp/transact! this
                         [(api/uncheck-all {:list-id list-id})])
        check-all
        #(comp/transact! this
                         [(api/check-all {:list-id list-id})])
        item->react-elem
        (fn [item]
          (let [on-change #(comp/transact! this [(api/toggle {:list-id list-id
                                                              :item-id (get item item-id-key)})])]
            (apply item-element [(comp/computed item {:checked? (contains? checked-set (get item item-id-key))
                                                      :onChange on-change})])))
        on-delete
        #(comp/transact! this [(api/delete-checked-items-from-list {:list-id list-id})])]
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
      (mapv item->react-elem items)))))

(defsc PersonList [this {:list/keys [id items] :as params}]
  {:query [:list/id
           {:list/items (comp/get-query Person)}
           :ui/checked-set]
   :ident :list/id}
  (ListComponent {:this this, :list-id id, :items items, :checked-set (:ui/checked-set params),
                  :item-id-key :person/id :item-element ui-person}))

(def ui-person-list (comp/factory PersonList))


(defsc CakeList [this {:list/keys [id items] :as params}]
  {:query [:list/id
           {:list/items (comp/get-query Cake)}
           :ui/checked-set]
   :ident :list/id}
  (ListComponent {:this this, :list-id id, :items items, :checked-set (:ui/checked-set params),
                  :item-id-key :cake/id :item-element ui-cake}))

(def ui-cake-list (comp/factory CakeList))


(defsc Root [_ data]
  {:query [{:root/buddies (comp/get-query PersonList)}
           {:root/foes (comp/get-query PersonList)}
           {:root/deserts (comp/get-query CakeList)}]
   :initial-state {}}
  (dom/div
   (ui-person-list (:root/buddies data))
   (ui-person-list (:root/foes data))
   (ui-cake-list (:root/deserts data))))


