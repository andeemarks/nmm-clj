(ns morris.common.board
  (:require [clojure.string :as str]
            [loom.alg :refer :all]
            [loom.graph :refer :all]
            [morris.be.piece :as piece]
            [taoensso.timbre :as log]))

(defn board [] 
	(graph 
		; outer square
		[ :a1 :d1] [ :d1 :g1] [ :g1 :g4] [ :g4 :g7] [ :g7 :d7] [ :d7 :a7] [ :a7 :a4] [ :a4 :a1]
		; inner square
		[ :c3 :d3] [ :c3 :c4] [ :d3 :e3] [ :e3 :e4] [ :c4 :c5] [ :e4 :e5] [ :c5 :d5] [ :d5 :e5]
		; middle square
		[ :b2 :d2] [ :d2 :f2] [ :b2 :b4] [ :b4 :b6] [ :b6 :d6] [ :d6 :f6] [ :f6 :f4] [ :f4 :f2]
		; connectors
		[ :b4 :c4] [ :f4 :e4] [ :d2 :d3] [ :d6 :d5] [ :d1 :d2] [ :a4 :b4] [ :d7 :d6] [ :g4 :f4]
		))

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
