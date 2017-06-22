(ns morris.core-test
  (:require [midje.sweet :refer :all]
            [morris.core :refer :all]))

(facts "completed-mill?-mills"
  (fact "have all three positions occupied"
    (completed-mill? [true, true, true]) => true
    (completed-mill? [false, true, true]) => false
    (completed-mill? [true, false, true]) => false
    (completed-mill? [true, true, false]) => false
    (completed-mill? [true, false, false]) => false
    (completed-mill? [false, false, false]) => false
))

(facts "potential-mill?-mills"
  (fact "have one position unoccupied"
    (potential-mill? [true, true, true]) => false
    (potential-mill? [false, true, true]) => true
    (potential-mill? [true, false, true]) => true
    (potential-mill? [true, true, false]) => true
    (potential-mill? [true, false, false]) => false
    (potential-mill? [false, false, false]) => false
))
