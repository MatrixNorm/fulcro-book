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
                                                      :item-id id})])]
    (dom/li
     (dom/input {:type "checkbox"
                 :checked (true? checked?)
                 :onChange on-change})
     (dom/label (str name " " surname)))))

(def ui-person (comp/factory Person {:keyfn :person/id}))


(defsc Cake [this {:cake/keys [id name country]}
             {:keys [checked? containing-list-id]}]
  {:query [:cake/id :cake/name :cake/country]
   :ident :cake/id}
  (let [on-change #(comp/transact! this [(api/toggle {:list-id containing-list-id
                                                      :item-id id})])]
    (dom/li
     (dom/input {:type "checkbox"
                 :checked (true? checked?)
                 :onChange on-change})
     (dom/label (str name " (" country ")")))))

(def ui-cake (comp/factory Cake {:keyfn :cake/id}))


(defn ??? [{:keys [this id items checked-set item-id-key item-element]}]
  (let [total-number (count items)
        checked-number (count checked-set)
        all-checked? (and (< 0 total-number)
                          (= checked-number total-number))
        uncheck-all #(comp/transact! this
                                     [(api/uncheck-all {:list-id id})])
        check-all #(comp/transact! this
                                   [(api/check-all {:list-id id})])
        item->react-elem (fn [item] (apply item-element [(comp/computed item {:containing-list-id id
                                                                              :checked? (contains? checked-set (get item item-id-key))})]))
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
      (mapv item->react-elem items)))))

(defsc PersonList [this {:list/keys [id items] :as params}]
  {:query [:list/id
           {:list/items (comp/get-query Person)}
           :ui/checked-set]
   :ident :list/id}
  (??? {:this this, :id id, :items items, :checked-set (:ui/checked-set params),
        :item-id-key :person/id :item-element ui-person}))

(def ui-person-list (comp/factory PersonList))


(defsc CakeList [this {:list/keys [id items] :as params}]
  {:query [:list/id
           {:list/items (comp/get-query Cake)}
           :ui/checked-set]
   :ident :list/id}
  (??? {:this this, :id id, :items items, :checked-set (:ui/checked-set params),
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


