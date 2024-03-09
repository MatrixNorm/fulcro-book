(ns app.server.resolvers
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.server.database :as db]))

;; Given :person/id, this can generate the details of a person
(pc/defresolver person-resolver [_ {:person/keys [id]}]
  {::pc/input  #{:person/id}
   ::pc/output [:person/name :person/age]}
  (get @db/people-table id))

;; Given a :list/id, this can generate a list label and the people
;; in that list (but just with their IDs)
(pc/defresolver list-resolver [_ {:list/keys [id]}]
  {::pc/input  #{:list/id}
   ::pc/output [:list/label {:list/people [:person/id]}]}
  (when-let [list (get @db/list-table id)]
    (assoc list
           :list/people (mapv (fn [id] {:person/id id}) (:list/people list)))))

(comment
  (when-let [x false] "two more weeks")
  (when-let [x 2] [x :brandon])
  (let [list (get db/list-table :friends)]
    (assoc list
           :list/people (mapv (fn [id] {:person/id id}) (:list/people list))))
  ;;
  )

(pc/defresolver friends-resolver [_ _]
  {::pc/output [{:friends [:list/id]}]}
  {:friends {:list/id :friends}})

(pc/defresolver enemies-resolver [_ _]
  {::pc/output [{:enemies [:list/id]}]}
  {:enemies {:list/id :enemies}})

(def resolvers [enemies-resolver
                friends-resolver
                list-resolver
                person-resolver])
