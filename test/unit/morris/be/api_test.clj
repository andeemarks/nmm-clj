(ns morris.be.api-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.be.api :refer :all]
            [morris.be.core :as core]
            [ring.mock.request :as mock]))

(log/set-config! {[:appenders :standard-out :enabled?] true})

(defn- parse-body [body]
  (cheshire/parse-string (slurp body) true))

(def ^:const new-game (core/init-game))

(fact "Test placing a piece returns a game-state containing the new piece and removes the piece from the pool"
  (let [game-state-as-json (cheshire/generate-string new-game)
        response (app (-> (mock/request :post "/game/piece/white-1/a1")
                          (mock/content-type "application/json")
                          (mock/body game-state-as-json)))
        body     (parse-body (:body response))]
    (:status response) => 200
    (:white-pieces new-game) => (contains "white-1")
    (:pieces-on-board new-game) => {}
    (:white-pieces body) =not=> (contains "white-1")
    (:pieces-on-board body) => {:a1 "white-1"}))

(fact "Test removing a piece returns a game-state containing the new piece"
  (let [game-after-move (assoc new-game :pieces-on-board {:a1 "black-1"})
        game-state-as-json (cheshire/generate-string game-after-move)
        response (app (-> (mock/request :delete "/game/piece/a1")
                          (mock/content-type "application/json")
                          (mock/body game-state-as-json)))
        body     (parse-body (:body response))]
    (:status response) => 200
    (:a1 (:pieces-on-board game-after-move)) => "black-1"
    (:a1 (:pieces-on-board body)) => nil))

(fact "Test moving a piece returns a game-state containing the piece in thw new position"
  (let [game-after-move (assoc new-game :pieces-on-board {:a1 "white-1"})
        game-state-as-json (cheshire/generate-string game-after-move)
        response (app (-> (mock/request :put "/game/piece/a1/a4")
                          (mock/content-type "application/json")
                          (mock/body game-state-as-json)))
        body     (parse-body (:body response))]
    (:status response) => 200
    (:a1 (:pieces-on-board game-after-move)) => "white-1"
    (:pieces-on-board game-after-move) =not=> (contains :a4)
    (:pieces-on-board body) =not=> (contains :a1)
    (:a4 (:pieces-on-board body)) => "white-1"))