(ns morris.fe.output-test
  (:require [midje.sweet :refer :all]
            [loom.attr :refer :all]
            [taoensso.timbre :as log]
            [morris.be.core :as core]
            [morris.be.piece :as piece]
            [morris.fe.output :refer :all]
            ))

(log/merge-config! {:appenders nil})

(facts "board layout"
  (fact "can show an empty board"
    (let [empty-position (attrs (layout (core/init-game)) :a1)]
      (:color empty-position) => "gray"))
  (fact "reflects game state on non-empty boards"
    (let [white-piece (attrs (layout (assoc (core/init-game) :game-state {:a1 :white-1})) :a1)]
      (:fillcolor white-piece) => "white")
    (let [black-piece (attrs (layout (assoc (core/init-game) :game-state {:d3 :black-3})) :d3)]
      (:fillcolor black-piece) => "black")))
