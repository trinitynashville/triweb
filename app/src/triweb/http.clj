(ns triweb.http
  (:require [clj-http.conn-mgr :as conn]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            [triweb.admin :as admin]
            [triweb.http.auth]
            [triweb.http.common :refer [text-response]]
            [triweb.http.routes :refer [handle-route router]]))

(defn wrap-log [app]
  (fn [req]
    (locking #'wrap-log
      (printf "%s %s\n"
               (.toString (java.util.Date.))
               (with-out-str (pr req))))
    (app req)))

;; the :ring :handler
(def app
  (-> router
      wrap-params
      (wrap-resource "static")
      (wrap-content-type)
      wrap-log))

(defn run [settings]
  (jetty/run-jetty
   app
   {:port 9000
    :send-server-version? false}))

(defmethod handle-route :ping
  [req]
  (text-response "pong"))
