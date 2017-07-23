(ns morris.common.board-test
  (:require [midje.sweet :refer :all]
            [loom.attr :refer :all]
            [taoensso.timbre :as log]
            [morris.be.core :as core]
            [morris.be.piece :as piece]
            [morris.common.board :refer :all]
            ))

(log/merge-config! {:appenders nil})

(facts "checking for neighbours"
  (fact "returns true when the locations are immediate neighbours"
    (neighbour? (board) :a1 :a4) => true)
  (fact "returns false when the locations are not connected"
    (neighbour? (board) :a1 :b2) => false)
  (fact "returns false when the locations are connected but not directly"
    (neighbour? (board) :a1 :a7) => false))

(facts "checking for legal piece movement"
  (fact "returns false if the origin is not occupied by the current player"
    (valid-move? "black" {:a1 :white-1} :a1 :a4)  => false)
  (fact "returns false if the origin is unoccupied"
    (valid-move? "white" {:a1 :white-1} :a4 :a7)  => false)
  (fact "returns false if the destination is occupied"
    (valid-move? "white" {:a1 :white-1} :a1 :a1)  => false)
  (fact "returns false if the destination is not adjacent to the origin"
    (valid-move? "white" {:a1 :white-1} :a1 :b2)  => false)
  (fact "returns true otherwise"
    (valid-move? "white" {:a1 :white-1} :a1 :a4)  => true))

(defn- n-white-pieces [n] (take n (piece/make-white-pieces)))
(defn- n-black-pieces [n] (take n (piece/make-black-pieces)))
(def all-black-pieces (n-black-pieces 10))
(def all-white-pieces (n-white-pieces 10))
(def empty-board-state nil)

(facts "checking for end game"
  (tabular
    (fact "returns true if the combination of played and pool pieces is less than three"
      (end-game? ?white-pieces ?black-pieces ?game-state) => ?expected)
      ?white-pieces       ?black-pieces       ?game-state       ?expected
      nil                 all-black-pieces    empty-board-state true
      (n-white-pieces 2)  all-black-pieces    empty-board-state true
      (n-white-pieces 1)  all-black-pieces    {:a1 :white-1}    true
      all-white-pieces    nil                 empty-board-state true
      all-white-pieces    (n-black-pieces 2)  empty-board-state true
      all-white-pieces    (n-black-pieces 1)  {:a1 :black-1}    true
      all-white-pieces    all-black-pieces    empty-board-state false
      (n-white-pieces 3)  all-black-pieces    empty-board-state false
      (n-white-pieces 2)  all-black-pieces    {:a1 :white-1}    false
      all-white-pieces    (n-black-pieces 3)  empty-board-state false
      all-white-pieces    (n-black-pieces 2) {:a1 :black-1}     false
      ))
