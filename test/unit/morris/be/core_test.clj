(ns morris.be.core-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.be.piece :as piece]
            [morris.be.core :refer :all]))

(log/merge-config! {:appenders nil})

(defn- n-white-pieces [n] (take n (piece/make-white-pieces)))
(defn- n-black-pieces [n] (take n (piece/make-black-pieces)))
(def all-black-pieces (n-black-pieces 10))
(def all-white-pieces (n-white-pieces 10))
(def empty-board-state {})

(facts "checking for end game"
  (tabular
    (fact "returns true if the combination of played and pool pieces is less than three"
      (end-game? ?white-pieces ?black-pieces ?pieces-on-board) => ?expected)
      ?white-pieces       ?black-pieces       ?pieces-on-board       ?expected
      nil                 all-black-pieces    empty-board-state true
      (n-white-pieces 2)  all-black-pieces    empty-board-state true
      (n-white-pieces 1)  all-black-pieces    {:a1 "white-1"}    true
      all-white-pieces    nil                 empty-board-state true
      all-white-pieces    (n-black-pieces 2)  empty-board-state true
      all-white-pieces    (n-black-pieces 1)  {:a1 "black-1"}    true
      all-white-pieces    all-black-pieces    empty-board-state false
      (n-white-pieces 3)  all-black-pieces    empty-board-state false
      (n-white-pieces 2)  all-black-pieces    {:a1 "white-1"}    false
      all-white-pieces    (n-black-pieces 3)  empty-board-state false
      all-white-pieces    (n-black-pieces 2) {:a1 "black-1"}     false
      ))

(facts "moving pieces"
  (let [game (init-game)
        after-move-1 (place-piece (init-game) (first (:white-pieces game)) :a1)]
    (fact "is illegal if the origin is not for the current player"
      (let [after-player-switch (assoc after-move-1 :current-player "black")]
        (move-piece after-player-switch :a1 :a4)  => (throws IllegalArgumentException)))
    (fact "is illegal if the origin is unoccupied"
      (move-piece after-move-1 :a4 :a7)  => (throws IllegalArgumentException))
    (fact "is illegal if the destination is occupied"
      (move-piece after-move-1 :a1 :a1)  => (throws IllegalArgumentException))
    (fact "is illegal if the destination is not adjacent to the origin"
      (move-piece after-move-1 :a1 :b2)  => (throws IllegalArgumentException))
    (fact "checks for mill completion"
      (let [game (init-game)
            after-move-1 (assoc game :pieces-on-board {:a1 "white-1" :a4 "white-2" :d7 "white-3"})]
        (:completed-mill-event (move-piece after-move-1 :d7 :a7)) => #{:a1 :a4 :a7}))
    (fact "returns a new game state when successful"
      (let [new-pieces-on-board (:pieces-on-board (move-piece after-move-1 :a1 :a4))]
        (:a1 new-pieces-on-board) => nil
        (:a4 new-pieces-on-board) => "white-1") )))

(facts "removing pieces"
  (fact "changes only the game state if the location is occupied by another player"
    (let [game (init-game)
          after-move-1 (place-piece game (first (:black-pieces game)) :a1)
          stub-completed-mill (assoc after-move-1 :completed-mill-event "foo")
          updated-game (remove-piece stub-completed-mill :a1)]
        (:white-pieces updated-game) => (:white-pieces stub-completed-mill) 
        (:black-pieces updated-game) => (:black-pieces stub-completed-mill) 
        (:completed-mill-event updated-game) => nil
        (:pieces-on-board updated-game) => {} )) )
  (fact "is illegal if the location is for the current player"
    (let [game (init-game)
          after-move-1 (place-piece game (first (:white-pieces game)) :a1)]
          (:current-player after-move-1) => "white"
          (remove-piece after-move-1 :a1) => (throws IllegalArgumentException)))
  (fact "will end the game if the opposition has less than three available pieces"
    (let [game (init-game)
          game-after-placing-piece-to-remove (place-piece game (first (:black-pieces game)) :a1)
          game-after-removing-white-piece-pool (update game-after-placing-piece-to-remove :black-pieces {})
          game-after-piece-removal (remove-piece game-after-removing-white-piece-pool :a1)]
        (:game-over-event game-after-piece-removal) =not=> nil) )
  (fact "is illegal if the location is not occupied"
    (let [game (init-game)]
      (remove-piece game :a1) => (throws IllegalArgumentException)))
  (future-fact "is illegal if the location is part of a completed mill when other options exist")

(facts "completing mills"
  (fact "will generate an event comtaining the completed mill"
    (let [game (init-game)
          after-move-1 (place-piece game (first (:white-pieces game)) :a1)
          after-move-2 (place-piece after-move-1 (second (:white-pieces after-move-1)) :a4)
          piece (nth (:white-pieces after-move-2) 3)]      
      (:completed-mill-event (place-piece after-move-2 piece :a7)) => #{:a1 :a4 :a7}))
  (fact "will only generate an event the first time the mill is completed"
    (let [game (init-game)
          after-move-1 (place-piece game (first (:white-pieces game)) :a1)
          after-move-2 (place-piece after-move-1 (second (:white-pieces after-move-1)) :a4)
          after-move-3 (place-piece after-move-2 (nth (:white-pieces after-move-2) 3) :a7)
          piece (nth (:white-pieces after-move-2) 4)]      
      (:completed-mill-event (place-piece after-move-3 piece :b2)) => nil))
  )

(facts "placing pieces"
  (fact "returns a new game state if the location is unoccupied"
    (let [game (init-game)
          updated-game (place-piece game (first (:white-pieces game)) :a1)]
       (:white-pieces updated-game) =not=> nil
       (:black-pieces updated-game) =not=> nil
       (:current-player updated-game) =not=> nil
       (:pieces-on-board updated-game) =not=> nil
       ))
  (fact "reduces the number of pieces of the corresponding player"
    (let [game (init-game)
          initial-white-piece-count (count (:white-pieces game))
          initial-black-piece-count (count (:black-pieces game))
          updated-game (place-piece game (first (:white-pieces game)) :a1)]
       (+ 1 (count (:white-pieces updated-game))) => initial-white-piece-count
       (count (:black-pieces updated-game)) => initial-black-piece-count ))
  (fact "is illegal if the location does not exist"
    (let [game (init-game)]
      (place-piece game (first (:white-pieces game)) :a11) => (throws IllegalArgumentException)))
  (fact "is illegal if the location is occupied"
    (let [game (init-game)
          updated-game (place-piece game (first (:white-pieces game)) :a1)
          piece (first (:black-pieces updated-game))]      
      (place-piece updated-game piece :a1) => (throws IllegalArgumentException)))
  )
