(ns morris.core-test
  (:require [midje.sweet :refer :all]
            [morris.core :refer :all]))

(facts "moving pieces"
  (let [game (init-game)
        after-move-1 (update-game (init-game) (first (:white-pieces game)) :a1)]
    (future-fact "is illegal if the origin is not for the current player"
      (move-piece after-move-1 :a4 :a7)  => (throws IllegalStateException))
    (fact "is illegal if the origin is unoccupied"
      (move-piece after-move-1 :a4 :a7)  => (throws IllegalStateException))
    (fact "is illegal if the destination is occupied"
      (move-piece after-move-1 :a1 :a1)  => (throws IllegalStateException))
    (fact "is illegal if the destination is not adjacent to the origin"
      (move-piece after-move-1 :a1 :b2)  => (throws IllegalStateException))
    (fact "checks for mill completion"
      (let [game (init-game)
            after-move-1 (assoc game :game-state {:a1 :white-1 :a4 :white-2 :d7 :white-3})]
        (:completed-mill-event (move-piece after-move-1 :d7 :a7)) => #{:a1 :a4 :a7}))
    (fact "returns a new game state when successful"
      (let [new-game-state (:game-state (move-piece after-move-1 :a1 :a4))]
        (:a1 new-game-state) => nil
        (:a4 new-game-state) => :white-1) )))

(facts "removing pieces"
  (fact "changes only the game state if the location is occupied by another player"
    (let [game (init-game)
          after-move-1 (update-game game (first (:white-pieces game)) :a1)
          stub-completed-mill (assoc after-move-1 :completed-mill-event "foo")
          updated-game (remove-piece stub-completed-mill :a1)]
        (:white-pieces updated-game) => (:white-pieces stub-completed-mill) 
        (:black-pieces updated-game) => (:black-pieces stub-completed-mill) 
        (:completed-mill-event updated-game) => nil
        (:game-state updated-game) => {} )) )
  (fact "will end the game if the opposition has less than three available pieces"
    (let [game (init-game)
          game-after-placing-piece-to-remove (update-game game (first (:white-pieces game)) :a1)
          game-after-removing-white-piece-pool (dissoc game-after-placing-piece-to-remove :white-pieces)
          game-after-piece-removal (remove-piece game-after-removing-white-piece-pool :a1)]
        (:game-over-event game-after-piece-removal) =not=> nil) )
  (future-fact "is illegal if the location is part of a completed mill")
  (fact "is illegal if the location is not occupied"
    (let [game (init-game)]
      (remove-piece game :a1) => (throws IllegalArgumentException)))

(facts "checking for end game"
  (fact "returns true if the combination of played and pool pieces is less than three"
    (let [initial-game (init-game)
          game-after-placing-white (update-game initial-game (first (:white-pieces initial-game)) :a1)
          game-after-placing-black (update-game initial-game (first (:black-pieces initial-game)) :a1)]
      (check-for-end-game initial-game) => false
      (check-for-end-game (dissoc initial-game :white-pieces)) => true
      (check-for-end-game (assoc initial-game :white-pieces (take 3 (:white-pieces initial-game)))) => false
      (check-for-end-game (assoc initial-game :white-pieces (take 2 (:white-pieces initial-game)))) => true
      (check-for-end-game (assoc game-after-placing-white :white-pieces (take 2 (:white-pieces initial-game)))) => false
      (check-for-end-game (dissoc initial-game :black-pieces)) => true
      (check-for-end-game (assoc initial-game :black-pieces (take 3 (:black-pieces initial-game)))) => false
      (check-for-end-game (assoc initial-game :black-pieces (take 2 (:black-pieces initial-game)))) => true
      (check-for-end-game (assoc game-after-placing-black :black-pieces (take 2 (:black-pieces initial-game)))) => false
    )))

(facts "completing mills"
  (fact "will generate an event comtaining the completed mill"
    (let [game (init-game)
          after-move-1 (update-game game (first (:white-pieces game)) :a1)
          after-move-2 (update-game after-move-1 (second (:white-pieces after-move-1)) :a4)
          piece (nth (:white-pieces after-move-2) 3)]      
      (:completed-mill-event (update-game after-move-2 piece :a7)) => #{:a1 :a4 :a7}))
  (fact "will only generate an event the first time the mill is completed"
    (let [game (init-game)
          after-move-1 (update-game game (first (:white-pieces game)) :a1)
          after-move-2 (update-game after-move-1 (second (:white-pieces after-move-1)) :a4)
          after-move-3 (update-game after-move-2 (nth (:white-pieces after-move-2) 3) :a7)
          piece (nth (:white-pieces after-move-2) 4)]      
      (:completed-mill-event (update-game after-move-3 piece :b2)) => nil))
  )

(facts "placing pieces"
  (fact "returns a new game state if the location is unoccupied"
    (let [game (init-game)
          updated-game (update-game game (first (:white-pieces game)) :a1)]
       (:white-pieces updated-game) =not=> nil
       (:black-pieces updated-game) =not=> nil
       (:game-state updated-game) =not=> nil
       ))
  (fact "reduces the number of pieces of the corresponding player"
    (let [game (init-game)
          initial-white-piece-count (count (:white-pieces game))
          initial-black-piece-count (count (:black-pieces game))
          updated-game (update-game game (first (:white-pieces game)) :a1)]
       (+ 1 (count (:white-pieces updated-game))) => initial-white-piece-count
       (count (:black-pieces updated-game)) => initial-black-piece-count ))
  (fact "is illegal if the location does not exist"
    (let [game (init-game)]
      (update-game game (first (:white-pieces game)) :a11) => (throws IllegalArgumentException)))
  (fact "is illegal if the location is occupied"
    (let [game (init-game)
          updated-game (update-game game (first (:white-pieces game)) :a1)
          piece (first (:black-pieces updated-game))]      
      (update-game updated-game piece :a1) => (throws IllegalStateException)))
  )
