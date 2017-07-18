(ns morris.board-test
  (:require [midje.sweet :refer :all]
            [loom.attr :refer :all]
            [morris.core :as core]
            [morris.board :refer :all]
            ))

(facts "checking for neighbours"
  (fact "returns true when the locations are immediate neighbours"
    (neighbour? (:board (core/init-game)) :a1 :a4) => true)
  (fact "returns false when the locations are not connected"
    (neighbour? (:board (core/init-game)) :a1 :b2) => false)
  (fact "returns false when the locations are connected but not directly"
    (neighbour? (:board (core/init-game)) :a1 :a7) => false))

(facts "checking for legal piece movement"
  (fact "returns false if the origin is unoccupied"
    (valid-move? {:a1 :white-1} :a4 :a7)  => false)
  (fact "returns false if the destination is occupied"
    (valid-move? {:a1 :white-1} :a1 :a1)  => false)
  (fact "returns false if the destination is not adjacent to the origin"
    (valid-move? {:a1 :white-1} :a1 :b2)  => false)
  (fact "returns true otherwise"
    (valid-move? {:a1 :white-1} :a1 :a4)  => true))

(facts "board layout"
  (fact "can show an empty board"
    (let [empty-position (attrs (layout (core/init-game)) :a1)]
      (:color empty-position) => "gray"))
  (fact "reflects game state on non-empty boards"
    (let [white-piece (attrs (layout (assoc (core/init-game) :game-state {:a1 :white-1})) :a1)]
      (:fillcolor white-piece) => "white")
    (let [black-piece (attrs (layout (assoc (core/init-game) :game-state {:d3 :black-3})) :d3)]
      (:fillcolor black-piece) => "black")))
