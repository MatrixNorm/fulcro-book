(ns app.ui
  (:require
   [app.mutations :as api]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/name :person/age]
   :ident (fn [] [:person/id (:person/id props)])
   :initial-state (fn [{:keys [id name age]}]
                    {:person/id id :person/name name :person/age age})}
  (dom/li
   (dom/h5 (str name " (age: " age ")")
           (dom/button {:onClick #(onDelete id)} "X"))))

(def ui-person (comp/factory Person {:keyfn :person/name}))


(defsc PersonList [this {:list/keys [id label people] :as props}]
  {:query [:list/id :list/label {:list/people (comp/get-query Person)}]
   :ident (fn [] [:list/id (:list/id props)])
   :initial-state
   (fn [{:keys [id label]}]
     {:list/id     id
      :list/label  label
      :list/people (if (= label "Friends")
                     [(comp/get-initial-state Person {:id 1 :name "Sally" :age 32})
                      (comp/get-initial-state Person {:id 2 :name "Brandon" :age 81})]
                     [(comp/get-initial-state Person {:id 3 :name "Fred" :age 11})
                      (comp/get-initial-state Person {:id 4 :name "Bobby" :age 55})])})}

  (let [delete-person-cb
        (fn [person-id] (comp/transact! this
                                        [(api/delete-person {:list/id id 
                                                             :person/id person-id})]))
        make-person-elem
        (fn [person] (ui-person (comp/computed person
                                               {:onDelete delete-person-cb})))]
    (dom/div
     (dom/h4 label)
     (dom/ul (map make-person-elem people)))))

(def ui-person-list (comp/factory PersonList))


(defsc Root [this {:keys [friends enemies]}]
  {:query [{:friends (comp/get-query PersonList)}
           {:enemies (comp/get-query PersonList)}]
   :initial-state
   (fn [params] {:friends (comp/get-initial-state PersonList {:id :friends :label "Friends"})
                 :enemies (comp/get-initial-state PersonList {:id :enemies :label "Enemies"})})}
  (dom/div
   (ui-person-list friends)
   (ui-person-list enemies)))

(comment
  (-> app.ui/PersonList
      comp/get-query
      meta)
  ;;
  )

;; * An initial app state sets up a tree of data 
;;   for startup to match the UI tree.
;; * Component query and ident are used to normalize 
;;   this initial data into the database.
;; * The query is used to pull data from the 
;;   normalized db into the props of the active Root UI.
;; * Transactions invoke abstract mutations.
;;     * Mutations modify the (normalized) db.
;;     * Fulcro and React manage the UI to do a minimal refresh.

