(ns morris.game-test
  (:require [midje.sweet :refer :all]
            [morris.game :refer :all]
            [morris.core :as core]
            ))

(facts "validating a move"
  (fact "succeeds if the origin is occupied and the destination is not"
    (valid-move? "a1/a4" {:a1 "white-1"}) => truthy)
  (fact "fails if the origin is empty"
    (valid-move? "a1/a4" nil) => falsey)
  (fact "fails if the origin is not a valid location"
    (valid-move? "aa/a4" nil) => falsey)
  (fact "fails if the destination is not a valid location"
    (valid-move? "a1/aa" nil) => falsey)
  (fact "fails if the destination is occupied"
    (valid-move? "a1/a4" {:a4 "black-3"}) => falsey))

(facts "choosing pieces from pool"
  (fact "will start with white pieces if no none have been played"
    (choose-piece (core/init-game)) => :white-1)
  (fact "will select the player with the most pieces still to play"
    (choose-piece (assoc (core/init-game) :black-pieces '(:black-3))) => :white-1
    (choose-piece (assoc (core/init-game) :white-pieces '(:white-4))) => :black-1
    ))