(ns morris.be.api-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.be.api :refer :all]
            [morris.be.core :as core]
            [ring.mock.request :as mock]))

(log/set-config! {[:appenders :standard-out :enabled?] true})

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(def ^:const new-game (core/init-game))

(fact "Test placing a piece returns a game-state containing the new piece"
  (let [game-state-as-json (cheshire/generate-string new-game)
        response (app (-> (mock/request :post "/game/piece/white-1/a1")
                          (mock/content-type "application/json")
                          (mock/body game-state-as-json)))
        body     (parse-body (:body response))]
    (:status response) => 200
    (:pieces-on-board body) => {:a1 "white-1"}))

(fact "Test removing a piece returns a game-state containing the new piece"
  (let [game-state-as-json (cheshire/generate-string (assoc new-game :pieces-on-board {:a1 "black-1"}))
        response (app (-> (mock/request :delete "/game/piece/a1")
                          (mock/content-type "application/json")
                          (mock/body game-state-as-json)))
        body     (parse-body (:body response))]
    (:status response) => 200
    (:a1 (:pieces-on-board body)) => nil))