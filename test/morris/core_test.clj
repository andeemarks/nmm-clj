(ns morris.core-test
  (:require [midje.sweet :refer :all]
            [morris.core :refer :all]))

(facts "removing pieces"
  (fact "changes only the game state if the location is occupied by another player"
    (let [game (init-game)
          after-move-1 (update-game game (first (:white-pieces game)) :a1)
          stub-completed-mill (assoc after-move-1 :completed-mill-event "foo")
          updated-game (remove-piece stub-completed-mill :a1)]
        (:board updated-game) => (:board stub-completed-mill) 
        (:white-pieces updated-game) => (:white-pieces stub-completed-mill) 
        (:black-pieces updated-game) => (:black-pieces stub-completed-mill) 
        (:completed-mill-event updated-game) => nil
        (:game-state updated-game) => {} )) )
  (fact "is illegal if the location is not occupied"
    (let [game (init-game)]
      (remove-piece game :a1) => (throws IllegalArgumentException)))

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
       (:board updated-game) =not=> nil
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
