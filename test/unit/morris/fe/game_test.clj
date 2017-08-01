(ns morris.fe.game-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.fe.game :refer :all]
            [morris.fe.input :as input]
            [morris.fe.api :as api]
            [morris.be.core :as core]
            ))

(log/merge-config! {:appenders nil})

(def ^:const game (core/init-game))

(facts "choosing a player"
  (fact "white goes if current player is black"
    (choose-player (assoc game :current-player "black")) => "white")
  (fact "black goes if current player is white"
    (choose-player (assoc game :current-player "white")) => "black"))

(facts "choosing pieces from pool"
  (fact "will start with white pieces if no none have been played"
    (choose-piece game) => "white-1")
  (fact "will select the player with the most pieces still to play"
    (choose-piece (assoc game :black-pieces '("black-3"))) => "white-1"
    (choose-piece (assoc game :white-pieces '("white-4"))) => "black-1"
    ))

(facts "finding pieces"
  (fact "with return all pieces for the specified player from the game state"
    (find-pieces game "white") => nil
    (find-pieces (assoc game :pieces-on-board {:a1 "white-1" :b2 "black-1"}) "white") => (just [:a1])
    (find-pieces (assoc game :pieces-on-board {:b2 "black-1"}) "white") => nil
    (find-pieces (assoc game :pieces-on-board {:b2 "black-1"}) "black") => (just [:b2])
    (find-pieces (assoc game :pieces-on-board {:a1 "white-1" :b2 "black-1" :f4 "white-3"}) "white") => (just [:a1 :f4])
    ))

(fact "switching players alternates between black and white"
  (:current-player game) => "white"
  (:current-player (switch-player game)) => "black"
  (:current-player (switch-player (switch-player game))) => "white"
  (:current-player (switch-player (switch-player (switch-player game)))) => "black")

(facts "moving pieces"
  (fact "calls api after a valid piece to move is provided"
    (let [game-state (assoc game :pieces-on-board {:a1 "white-1"})]
      (process-round "piece-movement" game-state "white-1") =not=> nil
      (provided
        (find-pieces anything anything) => "foo"
        (api/move-piece game-state :a1 :d1) => anything :times 1
        (input/for-player-move anything " What is your move (from/to) foo?") => {:origin :d1 :destination :a1} :times 1
        (input/for-player-move anything " That is not a valid move - what is your move (from/to) foo?") => {:origin :a1 :destination :d1} :times 1))))

(facts "removing pieces"
  (fact "calls api after a valid piece for removal is provided"
    (let [game-state (assoc game :pieces-on-board {:a1 "black-1"})]
      (process-round "piece-removal" game-state nil) =not=> nil
      (provided
        (find-pieces anything anything) => "foo"
        (api/remove-piece game-state :a1) => anything :times 1
        (input/for-piece anything " Mill completed! Which piece do you want to remove foo?" anything) => :d1 :times 1
        (input/for-piece anything " That is not a valid position - which piece to remove foo?" anything) => :a1 :times 1))))

(facts "placing pieces"
  (fact "calls api after a valid location for placement is provided"
    (let [game-state (assoc game :pieces-on-board {:a1 "black-1"})]
      (process-round "piece-placement" game-state "white-1") =not=> nil
      (provided
        (api/place-piece game-state "white-1" :d1) => anything :times 1
        (input/for-piece anything " Where do you want to place this piece?" anything) => :a1 :times 1
        (input/for-piece anything " That is not a valid position - where do you want to place this piece?" anything) => :d1 :times 1))))