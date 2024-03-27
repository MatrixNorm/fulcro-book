(ns app.client
  (:require
   [app.application :refer [APP]]
   [app.ui :as ui]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(def persons [{:person/id 1 :person/age 82
               :person/name "Joe" :person/surname "Brandon"}
              {:person/id 2 :person/age 79
               :person/name "Donny" :person/surname "Kushner"}
              {:person/id 3 :person/age 60
               :person/name "Bardak" :person/surname "Obmana"}
              {:person/id 4 :person/age 74
               :person/name "Bill" :person/surname "Kliptor"}
              {:person/id 5 :person/age 51
               :person/name "Crack" :person/surname "Hunter"}])

(def cakes [{:cake/id 1 :cake/name "Medovik" :cake/country "Russia"}
            {:cake/id 2 :cake/name "Karpatka" :cake/country "Poland"}
            {:cake/id 3 :cake/name "Paris–Brest" :cake/country "France"}])

(def initial-data-tree
  {:root/buddies {:list/id :buddies :list/items persons}
   :root/foes {:list/id :foes :list/items persons}
   :root/deserts {:list/id :deserts :list/items cakes}})

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. 
   See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! APP ui/Root "app")
  (merge/merge! APP initial-data-tree
                (comp/get-query app.ui/Root))

  (js/console.log "Loaded initially"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot 
   reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! APP ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! APP)
  (js/console.log "Hot reload"))