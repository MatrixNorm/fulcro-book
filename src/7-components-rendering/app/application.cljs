(ns app.application
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]))

(defonce APP (with-react18 (app/fulcro-app)))

(comment
  (keys APP)
  ;;
  )