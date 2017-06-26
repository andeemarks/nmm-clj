(ns morris.piece-test
  (:require [midje.sweet :refer :all]
            [morris.piece :refer :all]))

(facts "extract-colour"
  (fact "reverse engineers a piece's colour from it's id"
    (extract-colour (first (make-white-pieces))) => "white"
    (extract-colour (last (make-black-pieces))) => "black"))