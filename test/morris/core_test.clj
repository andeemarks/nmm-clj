(ns morris.core-test
  (:require [midje.sweet :refer :all]
            [morris.core :refer :all]))

(facts "moving pieces"
  (fact "is legal if the location is unoccupied"
    (let [game (init-game)]
      (update-game game (first (:white-pieces game)) :a1) =not=> nil))
  (fact "is illegal if the location is occupied"
    (let [game (init-game)
          updated-game (update-game game (first (:white-pieces game)) :a1)
          piece (first (:black-pieces updated-game))]      
      (update-game updated-game piece :a1) => (throws IllegalStateException)))
  )

(facts "completed-mills"
  (fact "have all three positions occupied"
    (completed-mill? [true, true, true]) => true
    (completed-mill? [false, true, true]) => false
    (completed-mill? [true, false, true]) => false
    (completed-mill? [true, true, false]) => false
    (completed-mill? [true, false, false]) => false
    (completed-mill? [false, false, false]) => false
))

(facts "potential-mills"
  (fact "have one position unoccupied"
    (potential-mill? [true, true, true]) => false
    (potential-mill? [false, true, true]) => true
    (potential-mill? [true, false, true]) => true
    (potential-mill? [true, true, false]) => true
    (potential-mill? [true, false, false]) => false
    (potential-mill? [false, false, false]) => false
))
