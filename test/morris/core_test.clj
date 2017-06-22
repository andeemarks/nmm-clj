(ns morris.core-test
  (:require [midje.sweet :refer :all]
            [morris.core :refer :all]))

(facts "completed-sets"
  (fact "have all three positions occupied"
    (completed? [true, true, true]) => true
    (completed? [false, true, true]) => false
    (completed? [true, false, true]) => false
    (completed? [true, true, false]) => false
    (completed? [true, false, false]) => false
    (completed? [false, false, false]) => false
))

(facts "potential-sets"
  (fact "have one position unoccupied"
    (potential? [true, true, true]) => false
    (potential? [false, true, true]) => true
    (potential? [true, false, true]) => true
    (potential? [true, true, false]) => true
    (potential? [true, false, false]) => false
    (potential? [false, false, false]) => false
))
