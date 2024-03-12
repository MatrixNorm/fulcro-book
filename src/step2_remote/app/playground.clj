(ns app.playground
  (:require
   [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro.algorithms.normalize :as fnorm]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

;; ;;;;;;;;;;;;;;;;;;;;;
;; ;; Denormalization ;;
;; ;;;;;;;;;;;;;;;;;;;;;

(comment
  ;; normalized database
  (def sample-db
    {:people [[:person/id 1] [:person/id 2]]
     :math/pi 3.14
     :person/id
     {1 {:person/name "Hunter" :person/age 50}
      2 {:person/name "Brandon" :person/age 80}}})

  (let [starting-node sample-db]
    (fdn/db->tree [:people :math/pi]
                  starting-node sample-db))

  (let [starting-node sample-db]
    (fdn/db->tree [:math/pi
                   {:people [:person/name]}]
                  starting-node sample-db))

  (let [starting-node sample-db]
    (fdn/db->tree [:person/id]
                  starting-node sample-db))

  ;; oops..
  (let [starting-node sample-db]
    (fdn/db->tree [{:person/id [:person/name]}]
                  starting-node sample-db))

  (let [starting-node sample-db]
    (fdn/db->tree [[:person/id 1] :math/pi]
                  starting-node sample-db))

  (let [starting-node sample-db]
    (fdn/db->tree [{[:person/id 1] [:person/name]}
                   :math/pi]
                  starting-node sample-db))
  ;;
  )

(comment
  (let [starting-entity {:person/name "Joe"
                         :person/age 42}
        ;; In this case I can supply an 
        ;; empty database for the final
        ;; argument because that database
        ;; is only used to resolve idents.
        empty-db {}]
    (fdn/db->tree [:person/name]
                  starting-entity empty-db))
  ;; same as
  (let [m {:person/name "Joe" :person/age 42}]
    (select-keys m [:person/name]))

  (let [starting-entity
        {:person/name "Joe"
         :person/age 42
         :person/spouse {:person/name "Judy"
                         :person/age 35}}
        empty-db {}]
    (fdn/db->tree
     [:person/name {:person/spouse [:person/age]}]
     starting-entity empty-db))
  ;;
  )

;; Idents as a Query Element
(comment
  (def sample-db
    {:people [[:person/id 1] [:person/id 2]]
     :math/e 2.71
     :person/id
     {1 {:person/name "Bob" :person/spouse [:person/id 2]}
      2 {:person/name "Judy"}}})

  (let [starting-entity {}]
      ;; starting entity is irrelevant, because 
      ;; the given EQL says "Join to a particular 
      ;; entity in the database"
    (fdn/db->tree [[:person/id 1]]
                  starting-entity sample-db))

  (let [starting-entity sample-db]
    (fdn/db->tree [[:person/id 1] :math/e]
                  starting-entity sample-db))

  (let [starting-entity {}]
    (fdn/db->tree
     [{[:person/id 1] [:person/name
                       {:person/spouse [:person/name]}]}]
     starting-entity sample-db))

  (let [starting-entity (get-in sample-db [:person/id 1])]
    (fdn/db->tree [:person/name]
                  starting-entity sample-db))
  ;;
  )

;; ;;;;;;;;;;;;;;;;;;;
;; ;; Normalization ;;
;; ;;;;;;;;;;;;;;;;;;;

(comment
  (defsc X [this props]
    {:ident (fn [] [:x 1])})

  (let [options  (comp/component-options X)
        ident-fn (get options :ident)]
    (ident-fn X {}))

  (comp/get-ident X {})
  ;;
  )

(comment
  (defsc Person [this props]
    {:query [:person/id :person/name]
     :ident :person/id})

  (defsc Root [this props]
    {:query [{:root/people (comp/get-query Person)}]})

  (comp/get-query Root)

  (fnorm/tree->db
   Root
   {:root/people {:person/id 1 :person/name "Bob"}}
   true)
  ;;
  )

;;;;;;;;;;;;;;;;;;;;;;;;
;; Evolving the Graph ;;
;;;;;;;;;;;;;;;;;;;;;;;;

(comment
  (defsc Person [this props]
    {:query [:person/id :person/name]
     :ident :person/id
     :initial-state
     (fn [params] {:person/id (:id params)
                   :person/name (:name params)})})

  (defsc Root [this props]
    {:query [{:root/people (comp/get-query Person)}]
     :initial-state
     (fn [_] {:root/people
              [(comp/get-initial-state
                Person {:id 1 :name "Bob"})
               (comp/get-initial-state
                Person {:id 2 :name "Judy"})]})})

  (def app (app/fulcro-app))

  (app/mount! app Root :headless)

  (app/current-state app)
  (merge/merge-component!
   app Person {:person/id 3 :person/name "Sally"})
  (app/current-state app)
  ;;
  )