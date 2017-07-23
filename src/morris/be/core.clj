(ns morris.be.core
  (:require [morris.common.board :as board]
            [morris.be.mill :as mill]
            [morris.be.piece :as piece]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(defn init-game []
	(log/info "*** New game ***")
	{:mode :piece-placement
		:current-player "white"
		:white-pieces (piece/make-white-pieces)
		:black-pieces (piece/make-black-pieces)
		:game-state nil})

(defn remove-piece-from-pool [game piece-on-board]
	(-> game
		(assoc :white-pieces (remove #(= piece-on-board %) (:white-pieces game)))
		(assoc :black-pieces (remove #(= piece-on-board %) (:black-pieces game)))))

(defn update-game-for-move [game piece-to-move origin destination]
	(-> game
		(assoc-in [:game-state destination] piece-to-move)
		(update-in [:game-state] dissoc origin)))

(defn- handle-mill-completion-event [game destination]
	(let [completed-mills (mill/find-completed-mills (:game-state game) destination)]
		(if completed-mills
			(assoc game :completed-mill-event (first completed-mills))
			(assoc game :completed-mill-event nil))))

(defn end-game? [white-pieces black-pieces game-state]
	(let [white-piece-pool-size (count white-pieces)
				black-piece-pool-size (count black-pieces)
				white-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":white") game-state))
				black-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":black") game-state))
				insufficient-white-pieces? (< (+ white-pieces-on-board-count white-piece-pool-size) 3)
				insufficient-black-pieces? (< (+ black-pieces-on-board-count black-piece-pool-size) 3)]
		(or insufficient-white-pieces? insufficient-black-pieces?)))

(defn- handle-end-game-event [game]
	(let [game-finished? (end-game? (:white-pieces game) (:black-pieces game) (:game-state game))]
		(if game-finished?
			(assoc game :game-over-event true)
			(assoc game :game-over-event nil))))

(defn move-piece [game origin destination]
	(let [piece-to-move (origin (:game-state game))]
		(log/info "Moving " piece-to-move " from " origin " to " destination)
		(if (board/valid-move? (:current-player game) (:game-state game) origin destination)
			(-> game
				(update-game-for-move piece-to-move origin destination)
				(handle-mill-completion-event destination))
			(throw (IllegalArgumentException. (str "Cannot move " piece-to-move " from " origin " to " destination))))))

(defn- add-piece-to-game [game destination piece] (update-in game [:game-state] merge {destination piece}))

(defn place-piece [game piece destination]
	(log/info "Attempting to place " piece " on " destination)
	(if (board/valid-placement? destination (:game-state game))				
		(-> game 
			(add-piece-to-game destination piece)
			(remove-piece-from-pool piece)
			(handle-mill-completion-event destination))
		(throw (IllegalArgumentException. (str "Piece " piece " cannot be placed on location " destination)))))

(defn- remove-piece-from-game [game piece] (update-in game [:game-state] dissoc piece))
(defn- clear-events [game] (dissoc game :completed-mill-event))

(defn remove-piece [game location-containing-piece]
	(log/info "Attempting to remove piece from " location-containing-piece)
	(if (board/valid-removal? (:current-player game) location-containing-piece (:game-state game))
		(-> game
			(remove-piece-from-game location-containing-piece)
			(clear-events)
			(handle-end-game-event))
		(throw (IllegalArgumentException. (str "Location " location-containing-piece " cannot be removed by " (:current-player game))))))