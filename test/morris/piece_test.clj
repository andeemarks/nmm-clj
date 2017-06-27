(ns morris.piece-test
  (:require [midje.sweet :refer :all]
            [morris.piece :refer :all]))

(facts "piece-generation"
	(fact "produces 9 pieces of the specified colour"
		(count (make-black-pieces)) => 9))
(facts "from-same-player"
	(facts "checks whether a set of piece ids have the same colour"
		(from-same-player? [:white-1]) => truthy
		(from-same-player? [:white-1 :white-1]) => truthy
		(from-same-player? [:white-1 :white-2]) => truthy
		(from-same-player? [:white-1 nil]) => truthy
		(from-same-player? [:white-1 :black-2]) => falsey
		(from-same-player? [:white-1 :whiter-2]) => falsey
		(from-same-player? []) => falsey))
(facts "extract-colour"
  (fact "reverse engineers a piece's colour from it's id"
    (extract-colour (first (make-white-pieces))) => "white"
    (extract-colour (last (make-black-pieces))) => "black"))