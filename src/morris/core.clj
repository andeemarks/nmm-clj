(ns morris.core
	(:require 
		[loom.graph :refer :all]
		[loom.io :refer :all]
		))

(defn make-white-piece [id] (str :white "-" id))
(defn make-black-piece [id] (str :black "-" id))

(defn make-location [id] id)

(defn connect-from-to [from to]
	[(make-location from) (make-location to)])

(defn- board [] 
	(graph 
		; outer square
		(connect-from-to :a1 :d1) (connect-from-to :d1 :g1) (connect-from-to :g1 :g4) (connect-from-to :g4 :g7) (connect-from-to :g7 :d7) (connect-from-to :d7 :a7) (connect-from-to :a7 :a4) (connect-from-to :a4 :a1)
		; inner square
		(connect-from-to :c3 :d3) (connect-from-to :c3 :c4) (connect-from-to :d3 :e3) (connect-from-to :e3 :e4) (connect-from-to :c4 :c5) (connect-from-to :e4 :e5) (connect-from-to :c5 :d5) (connect-from-to :d5 :e5)
		; middle square
		(connect-from-to :b2 :d2) (connect-from-to :d2 :f2) (connect-from-to :b2 :b4) (connect-from-to :b4 :b6) (connect-from-to :b6 :d6) (connect-from-to :d6 :f6) (connect-from-to :f6 :f4) (connect-from-to :f4 :f2)
		; connectors
		(connect-from-to :b4 :c4) (connect-from-to :f4 :e4) (connect-from-to :d2 :d3) (connect-from-to :d6 :d5) (connect-from-to :d1 :d2) (connect-from-to :a4 :b4) (connect-from-to :d7 :d6) (connect-from-to :g4 :f4)))

(defn init-game []
	(let [board (board)
				white-pieces (map #(make-white-piece %) (range 1 9))
				black-pieces (map #(make-black-piece %) (range 1 9))
				game-state nil]
				{:board board 
					:white-pieces white-pieces 
					:black-pieces black-pieces 
					:game-state game-state}))

(defn make-mill [loc1 loc2 loc3]
	(subgraph (board) [(make-location loc1) (make-location loc2) (make-location loc3)]))

(def mills [
	; horizontal mills
	(make-mill :a1 :d1 :g1)
	(make-mill :b2 :d2 :f1)
	(make-mill :c3 :d3 :e3)
	(make-mill :a4 :b4 :c4)
	(make-mill :e4 :f4 :g4)
	(make-mill :c5 :d5 :e5)
	(make-mill :b6 :d6 :f6)
	(make-mill :a7 :d7 :g7)
	; vertical mills
	(make-mill :a1 :a4 :a7)
	(make-mill :b2 :b4 :b6)
	(make-mill :c3 :c4 :c5)
	(make-mill :d1 :d2 :d3)
	(make-mill :d5 :d6 :d7)
	(make-mill :e3 :e4 :e5)
	(make-mill :f2 :f4 :f6)
	(make-mill :g1 :g4 :g7)
	])


(defn completed-mill? [mills]
	(= 3 ((frequencies mills) true)))

(defn potential-mill? [mills]
	(= 1 ((frequencies mills) false)))

(defn update-game [game piece destination]
	(let [current-game-state (:game-state game)
				destination-available? (nil? (destination current-game-state))]
		(if destination-available?
			(assoc game :game-state (merge current-game-state {destination piece}))
			(throw (IllegalStateException. (str "Location " destination " is already occupied"))))))
