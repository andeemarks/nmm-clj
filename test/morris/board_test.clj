(ns morris.board-test
  (:require [midje.sweet :refer :all]
            [loom.graph :refer :all]
            [loom.attr :refer :all]
            [morris.board :refer :all]))

(facts "board layout"
  (fact "can show an empty board"
    (layout (board) nil) => anything)
  (fact "reflects game state on non-empty boards"
    (:color (attrs (layout (board) {:a1 :white-1}) :a1)) => "white"
    (:color (attrs (layout (board) {:d3 :black-3}) :d3)) => "black"))

(println (show (board) {:a1 :white-1}))