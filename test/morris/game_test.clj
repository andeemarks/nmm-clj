(ns morris.game-test
  (:require [midje.sweet :refer :all]
            [morris.game :refer :all]
            [morris.core :as core]
            ))

(fact "decomposing a move separates the origin and destination"
  (move-components "a1/a4") => {:origin :a1 :destination :a4}
  (move-components "a1") => {:origin nil :destination nil}
  (move-components "/a1") => {:origin nil :destination nil}
  (move-components " a1/a4 ") => {:origin :a1 :destination :a4}
  (move-components " a1 / a4 ") => {:origin :a1 :destination :a4}
  (move-components "A1/B4") => {:origin :a1 :destination :b4}
  )

(facts "choosing pieces from pool"
  (fact "will start with white pieces if no none have been played"
    (choose-piece (core/init-game)) => :white-1)
  (fact "will select the player with the most pieces still to play"
    (choose-piece (assoc (core/init-game) :black-pieces '(:black-3))) => :white-1
    (choose-piece (assoc (core/init-game) :white-pieces '(:white-4))) => :black-1
    ))