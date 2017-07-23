(ns morris.common.board
  (:require [clojure.string :as str]
            [loom.alg :refer :all]
            [loom.graph :refer :all]
            [morris.be.piece :as piece]
            [taoensso.timbre :as log]))

(defn make-location [id] id)

(defn connect-from-to [from to]
	[(make-location from) (make-location to)])

(defn board [] 
	(graph 
		; outer square
		(connect-from-to :a1 :d1) (connect-from-to :d1 :g1) (connect-from-to :g1 :g4) (connect-from-to :g4 :g7) (connect-from-to :g7 :d7) (connect-from-to :d7 :a7) (connect-from-to :a7 :a4) (connect-from-to :a4 :a1)
		; inner square
		(connect-from-to :c3 :d3) (connect-from-to :c3 :c4) (connect-from-to :d3 :e3) (connect-from-to :e3 :e4) (connect-from-to :c4 :c5) (connect-from-to :e4 :e5) (connect-from-to :c5 :d5) (connect-from-to :d5 :e5)
		; middle square
		(connect-from-to :b2 :d2) (connect-from-to :d2 :f2) (connect-from-to :b2 :b4) (connect-from-to :b4 :b6) (connect-from-to :b6 :d6) (connect-from-to :d6 :f6) (connect-from-to :f6 :f4) (connect-from-to :f4 :f2)
		; connectors
		(connect-from-to :b4 :c4) (connect-from-to :f4 :e4) (connect-from-to :d2 :d3) (connect-from-to :d6 :d5) (connect-from-to :d1 :d2) (connect-from-to :a4 :b4) (connect-from-to :d7 :d6) (connect-from-to :g4 :f4)
		))

(defn make-mill [loc1 loc2 loc3]
	(subgraph (board) [(make-location loc1) (make-location loc2) (make-location loc3)]))

(def mills [
	; horizontal mills
	(make-mill :a1 :d1 :g1)
	(make-mill :b2 :d2 :f2)
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

(defn location-exists? [location]
	(let [result (has-node? (board) location)]
		(log/debug "location-exists? for " location "=> " result)
		result))

(defn location-available? [location game-state]
	(let [result (nil? (location game-state))]
		(log/debug "location-available? for " location " with " game-state "=> " result)
		result))

(defn neighbour? [board loc1 loc2]
	(let [result (= 2 (count (bf-path board loc1 loc2)))]
		(log/debug "neighbour? of " loc1 " and " loc2 "=> " result)
		result))

(defn occupied-by-current-player? [current-player occupant]
	(if occupant
		(let [result (piece/is-from-player? occupant current-player)]
			(log/debug "occupied-by-current-player? of " current-player " and " occupant "=> " result)
			result)
		false))

(defn valid-placement? [destination game-state]
	(and 
		(location-available? destination game-state)
		(location-exists? destination)))

(defn valid-removal? [current-player location-to-remove game-state]
	(and
		(not (occupied-by-current-player? current-player (location-to-remove game-state)))
		(not (location-available? location-to-remove game-state))))

(defn valid-move? [current-player game-state origin destination]
	(and 
		(location-exists? origin)
		(location-available? destination game-state)
		(neighbour? (board) origin destination)
		(occupied-by-current-player? current-player (origin game-state))
		(not (location-available? origin game-state))))

(defn end-game? [white-pieces black-pieces game-state]
	(let [white-piece-pool-size (count white-pieces)
				black-piece-pool-size (count black-pieces)
				white-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":white") game-state))
				black-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":black") game-state))
				insufficient-white-pieces? (< (+ white-pieces-on-board-count white-piece-pool-size) 3)
				insufficient-black-pieces? (< (+ black-pieces-on-board-count black-piece-pool-size) 3)]
		(or insufficient-white-pieces? insufficient-black-pieces?)))
