(ns morris.board-test
  (:require [midje.sweet :refer :all]
            [loom.graph :refer :all]
            [loom.attr :refer :all]
            [morris.board :refer :all]))

(facts "board layout"
  (fact "can show an empty board"
    (let [empty-position (attrs (layout (board) nil) :a1)]
      (:color empty-position) => "gray"))
  (fact "reflects game state on non-empty boards"
    (let [white-piece (attrs (layout (board) {:a1 :white-1}) :a1)]
      (:fillcolor white-piece) => "white")
    (let [black-piece (attrs (layout (board) {:d3 :black-3}) :d3)]
      (:fillcolor black-piece) => "black")))

(spit "board.dot" (show (board) {:a1 :black-1 :d6 :white-1 :f2 :black-2 :b4 :black-2 :f4 :black-3 :c5 :white-3}))