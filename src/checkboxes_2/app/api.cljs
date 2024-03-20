(ns app.api
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defn set-checked* [state-atom list-id value]
  (let [list-of-ids (map second (get @state-atom list-id))
        person-table (:person/id @state-atom)
        person-table-new (reduce #(assoc-in %1 [%2 :ui/checked?] value)
                                 person-table list-of-ids)]
    (swap! state-atom #(assoc % :person/id person-table-new))))

(defmutation check-all
  [{:keys [list-id]}]
  (action [{:keys [state]}]
          (set-checked* state list-id true)))

(defmutation uncheck-all
  [{:keys [list-id]}]
  (action [{:keys [state]}]
          (set-checked* state list-id false)))

(defmutation toggle
  [{:keys [list-id person-id]}]
  (action [{:keys [state]}]
          (let [checked-set (get-in @state [:list/id list-id :ui/checked-set] #{})
                new-checked-set (if (contains? checked-set person-id)
                                  (disj checked-set person-id)
                                  (conj checked-set person-id))]
            (println checked-set new-checked-set)
            (swap! state assoc-in [:list/id list-id :ui/checked-set] new-checked-set))))


