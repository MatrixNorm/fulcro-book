(ns app.application
  (:require
   [com.fulcrologic.fulcro.application :as appl]
   [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
   [com.fulcrologic.fulcro.networking.http-remote :as http]))

(defonce APP (-> (appl/fulcro-app {:remotes {:remote (http/fulcro-http-remote {})}})
                 ;((fn [a] (def X1 a) a))
                 (with-react18)))

(comment
  (-> APP 
      (::com.fulcrologic.fulcro.application/state-atom)
      deref)
  
  )