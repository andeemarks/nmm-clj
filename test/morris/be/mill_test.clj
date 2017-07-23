(ns morris.be.mill-test
  (:require [midje.sweet :refer :all]
            [morris.be.core :as core]
            [morris.be.mill :refer :all]))

(facts "checking for completed mills"
  (fact "will return the graph for the completed mill if one has occurred"
    (let [game (core/init-game)]
      (empty? (find-completed-mills nil :a7)) => truthy
      (empty? (find-completed-mills {} :a7)) => truthy
      (empty? (find-completed-mills {:a1 :white-1} :a7)) => truthy
      (empty? (find-completed-mills {:a1 :white-1 :a4 :white-2 } :a7)) => truthy
      (empty? (find-completed-mills {:a1 :white-1 :a4 :white-2 :a7 :black-3} :a7)) => truthy
      (empty? (find-completed-mills {:a1 :white-1 :a4 :white-2 :a7 :white-3} :b2)) => truthy
      (first (find-completed-mills {:a1 :white-1 :a4 :white-2 :a7 :white-3} :a7)) => #{:a1 :a4 :a7}))
  (fact "will ignore already completed mills"
    (let [game (core/init-game)]
      (empty? (find-completed-mills {:a1 :white-1 :a4 :white-2 :a7 :white-3} :b2)) => truthy)))
