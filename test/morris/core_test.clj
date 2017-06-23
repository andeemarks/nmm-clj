(ns morris.core-test
  (:require [midje.sweet :refer :all]
            [morris.core :refer :all]))

(facts "completing mills"
  (fact "will generate an event"
    (let [game (init-game)
          after-move-1 (update-game game (first (:white-pieces game)) :a1)
          after-move-2 (update-game after-move-1 (second (:white-pieces after-move-1)) :a4)
          piece (nth (:white-pieces after-move-2) 3)]      
      (:event (update-game after-move-2 piece :a7)) => "mill completed"))
  (fact "will only generate an event the first time the mill is completed"
    (let [game (init-game)
          after-move-1 (update-game game (first (:white-pieces game)) :a1)
          after-move-2 (update-game after-move-1 (second (:white-pieces after-move-1)) :a4)
          after-move-3 (update-game after-move-2 (nth (:white-pieces after-move-2) 3) :a7)
          piece (nth (:white-pieces after-move-2) 4)]      
      (:event (update-game after-move-3 piece :b2)) => nil))
  )

(future-facts "moving pieces"
  (fact "is legal if the location is unoccupied"
    (let [game (init-game)]
      (update-game game (first (:white-pieces game)) :a1) =not=> nil))
  (fact "is illegal if the location does not exist"
    (let [game (init-game)]
      (update-game game (first (:white-pieces game)) :a11) => (throws IllegalArgumentException)))
  (fact "is illegal if the location is occupied"
    (let [game (init-game)
          updated-game (update-game game (first (:white-pieces game)) :a1)
          piece (first (:black-pieces updated-game))]      
      (update-game updated-game piece :a1) => (throws IllegalStateException)))
  )
