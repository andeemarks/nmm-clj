(ns morris.be.piece-test
  (:require [midje.sweet :refer :all]
            [morris.be.piece :refer :all]))

(facts "piece-checking"
	(fact "can tell whether a piece is white or black"
		(is-from-player? :white-1 "white") => true
		(is-from-player? :black-3 "black") => true
		(is-from-player? :black-3 "white") => false
		) )

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