(ns morris.be.api-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [morris.be.api :refer :all]
            [morris.be.core :as core]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(fact "Test placing a piece returns a game-state containing the new piece"
  (let [game-state-as-json (cheshire/generate-string (core/init-game))
        response (app (-> (mock/request :post "/game/piece/white-1/a1")
                          (mock/content-type "application/json")
                          (mock/body game-state-as-json)))
        body     (parse-body (:body response))]
    (:status response) => 200
    (:pieces-on-board body) => {:a1 "white-1"}))