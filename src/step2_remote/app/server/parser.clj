(ns app.server.parser
  (:require
   [app.server.resolvers]
   [app.server.mutations]
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc]
   [taoensso.timbre :as log]))

(def resolvers [app.server.resolvers/resolvers
                app.server.mutations/mutations])

(def pathom-parser
  (p/parser {::p/env {::p/reader [p/map-reader
                                  pc/reader2
                                  pc/ident-reader
                                  pc/index-reader]
                      ::pc/mutation-join-globals [:tempids]}
             ::p/mutate  pc/mutate
             ::p/plugins [(pc/connect-plugin {::pc/register [resolvers]})
                          p/error-handler-plugin
                          ;; or p/elide-special-outputs-plugin
                          (p/post-process-parser-plugin p/elide-not-found)]}))

(defn api-parser [query]
  (log/info "Process" query)
  (pathom-parser {} query))

(comment

  (api-parser ['(app.mutations/delete-person
                 {:list/id :friends, :person/id 1})])

  (api-parser [{[:person/id 1]
                [:person/name :person/age]}])

  (api-parser [{[:list/id :friends]
                [:list/id :list/label]}])

  (api-parser
   [{[:list/id :friends]
     [:list/id :list/label :list/people]}])

  (api-parser
   [{[:list/id :friends]
     [:list/id {:list/people [:person/name]}]}])

  (api-parser
   [{:friends
     [:list/id {:list/people [:person/name]}]}])
  ;;
  )