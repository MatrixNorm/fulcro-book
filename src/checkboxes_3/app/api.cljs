(ns app.api
  (:require
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation check-all
  [{:keys [list-id]}]
  (action [{:keys [state]}]
          (let [all-items-ids (get-in @state [:list/id list-id :list/items])
                new-checked-set (set (map second all-items-ids))]
            (swap! state assoc-in [:list/id list-id :ui/checked-set] new-checked-set))))

(defmutation uncheck-all
  [{:keys [list-id]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:list/id list-id :ui/checked-set] #{})))

(defmutation toggle
  [{:keys [list-id item-id]}]
  (action [{:keys [state]}]
          (let [checked-set (get-in @state [:list/id list-id :ui/checked-set] #{})
                new-checked-set (if (contains? checked-set item-id)
                                  (disj checked-set item-id)
                                  (conj checked-set item-id))]
            (println checked-set new-checked-set)
            (swap! state assoc-in [:list/id list-id :ui/checked-set] new-checked-set))))

(defmutation delete-checked-items-from-list
  [{:keys [list-id]}]
  (action [{:keys [state]}]
          (let [checked-ids (get-in @state [:list/id list-id :ui/checked-set])
                new-list-fn (fn [old-list]
                              (vec (filter #(not (contains? checked-ids (second %))) old-list)))]
            (swap! state update-in [:list/id list-id :list/people] new-list-fn))))
