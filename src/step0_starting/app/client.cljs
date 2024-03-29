(ns app.client
  (:require
   [app.application :refer [APP]]
   [app.ui :as ui]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]))


(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. 
   See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! APP ui/Root "app")
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