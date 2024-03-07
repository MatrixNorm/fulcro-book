(ns app.server
  (:require
   [app.parser :refer [api-parser]]
   [org.httpkit.server :as http]
   [com.fulcrologic.fulcro.server.api-middleware :as fulcro]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.resource :refer [wrap-resource]]))

(def ^:private not-found-handler
  (fn [_]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "Not Found"}))

(def middleware
  (-> not-found-handler
      (fulcro/wrap-api {:uri    "/api"
                        :parser api-parser})
      (fulcro/wrap-transit-params)
      (fulcro/wrap-transit-response)
      (wrap-resource ".")
      wrap-content-type))

(defonce stop-fn (atom nil))

(defn ^:export start []
  (reset! stop-fn (http/run-server middleware {:port 3000})))

(defn ^:export stop []
  (when @stop-fn
    (@stop-fn)
    (reset! stop-fn nil)))