(ns app.client
  (:require
   [app.application :refer [APP]]
   [app.ui :as ui]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.application :as appl]
   [com.fulcrologic.fulcro.data-fetch :as df]))

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. 
   See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (appl/mount! APP ui/Root "app")
  (df/load! APP :friends ui/PersonList)
  (df/load! APP :enemies ui/PersonList)
  (js/console.log "Loaded initially"))

;; (transact! this [(df/internal-load!
;;                   {:source-key :person/all
;;                    :query (comp/get-query Person)
;;                    :remote :remote})])
;; should be exactly equivalent to
;; (df/load! this :person/all Person)


(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. 
   See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh
  (appl/mount! APP ui/Root "app")
  ;; 3.3.0+ Make sure dynamic queries are refreshed
  (comp/refresh-dynamic-queries! APP)
  (js/console.log "Hot reload"))