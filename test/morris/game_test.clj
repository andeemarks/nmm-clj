(ns morris.game-test
  (:require [midje.sweet :refer :all]
            [morris.game :refer :all]
            [morris.core :as core]
            ))

(facts "choosing pieces from pool"
  (fact "will start with white pieces if no none have been played"
    (choose-piece (core/init-game)) => :white-1)
  (fact "will select the player with the most pieces still to play"
    (choose-piece (assoc (core/init-game) :black-pieces '(:black-3))) => :white-1
    (choose-piece (assoc (core/init-game) :white-pieces '(:white-4))) => :black-1
    ))