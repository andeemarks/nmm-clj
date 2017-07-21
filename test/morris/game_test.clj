(ns morris.game-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.game :refer :all]
            [morris.core :as core]
            ))

(log/merge-config! {:appenders nil})

(facts "choosing a player"
  (fact "white goes if current player is black"
    (choose-player (assoc (init-game) :current-player "black")) => "white")
  (fact "black goes if current player is white"
    (choose-player (assoc (init-game) :current-player "white")) => "black"))

(facts "choosing pieces from pool"
  (fact "will start with white pieces if no none have been played"
    (choose-piece (init-game)) => :white-1)
  (fact "will select the player with the most pieces still to play"
    (choose-piece (assoc (init-game) :black-pieces '(:black-3))) => :white-1
    (choose-piece (assoc (init-game) :white-pieces '(:white-4))) => :black-1
    ))

(facts "finding pieces"
  (fact "with return all pieces for the specified player from the game state"
    (find-pieces (init-game) "white") => nil
    (find-pieces (assoc (init-game) :game-state {:a1 :white-1 :b2 :black-1}) "white") => (just [:a1])
    (find-pieces (assoc (init-game) :game-state {:b2 :black-1}) "white") => nil
    (find-pieces (assoc (init-game) :game-state {:b2 :black-1}) "black") => (just [:b2])
    (find-pieces (assoc (init-game) :game-state {:a1 :white-1 :b2 :black-1 :f4 :white-3}) "white") => (just [:a1 :f4])
    ))

(fact "switching players alternates between black and white"
  (:current-player (init-game)) => "white"
  (:current-player (switch-player (init-game))) => "black"
  (:current-player (switch-player (switch-player (init-game)))) => "white"
  (:current-player (switch-player (switch-player (switch-player (init-game))))) => "black")