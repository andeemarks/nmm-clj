(ns morris.mill-test
  (:require [midje.sweet :refer :all]
            [morris.core :as core]
            [morris.mill :refer :all]))

(facts "checking for completed mills"
  (fact "will return the graph for the completed mill if one has occurred"
    (let [game (core/init-game)]
      (check-for-completed-mills nil :a7) => false
      (check-for-completed-mills {} :a7) => false
      (check-for-completed-mills {:a1 :white-1} :a7) => false
      (check-for-completed-mills {:a1 :white-1 :a4 :white-2 } :a7) => false
      (check-for-completed-mills {:a1 :white-1 :a4 :white-2 :a7 :white-3} :a7) => true)))
