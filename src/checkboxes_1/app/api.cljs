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


