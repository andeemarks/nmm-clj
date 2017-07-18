(ns morris.core
	(:require 
		[morris.board :as board]
		[morris.mill :as mill]
		[clojure.string :as str]
		[morris.piece :as piece]
		))

(defn init-game []
	{:board (board/board)
		:white-pieces (piece/make-white-pieces)
		:black-pieces (piece/make-black-pieces)
		:game-state nil})

(defn remove-piece-from-pool [piece-on-board white-pieces black-pieces]
	{:board (board/board)
		:white-pieces (remove #(= piece-on-board %) white-pieces)
		:black-pieces (remove #(= piece-on-board %) black-pieces)
		:game-state nil})

(defn move-piece [game origin destination]
	(let [game-state (:game-state game)
				piece-to-move (origin game-state)]
		(if (and 
				(board/location-exists? origin)
				(board/location-available? destination game-state)
				(board/neighbour? (:board game) origin destination)
				(not (board/location-available? origin game-state)))
			(-> game
				(assoc-in [:game-state destination] piece-to-move)
				(update-in [:game-state] dissoc origin))
			(throw (IllegalStateException. (str "Cannot move from " origin " to " destination))))))

(defn update-game [game piece destination]
	(let [current-game-state (:game-state game)]

		(if (board/location-exists? destination)				
			(if (board/location-available? destination current-game-state)
				(let [new-game-state (merge current-game-state {destination piece})
							game-with-updated-player-pools (remove-piece-from-pool piece (:white-pieces game) (:black-pieces game))
							new-game (assoc game-with-updated-player-pools :game-state new-game-state)
							mill-completed? (mill/find-completed-mills new-game-state destination)]
					(if mill-completed?
						(assoc new-game :completed-mill-event (first mill-completed?))
						(assoc new-game :completed-mill-event nil)))
				(throw (IllegalStateException. (str "Location " destination " is already occupied"))))
			(throw (IllegalArgumentException. (str "Location " destination " does not exist on board"))))))

(defn check-for-end-game [game]
	(let [white-piece-pool-size (count (:white-pieces game))
				black-piece-pool-size (count (:black-pieces game))
				white-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":white") (:game-state game)))
				black-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":black") (:game-state game)))
				insufficient-white-pieces? (< (+ white-pieces-on-board-count white-piece-pool-size) 3)
				insufficient-black-pieces? (< (+ black-pieces-on-board-count black-piece-pool-size) 3)]
		(or insufficient-white-pieces? insufficient-black-pieces?)))

(defn remove-piece [game location-containing-piece]
	(if (board/location-available? location-containing-piece (:game-state game))
		(throw (IllegalArgumentException. (str "Location " location-containing-piece " is not occupied")))
		(let [updated-game (-> game
												(update-in [:game-state] dissoc location-containing-piece)
												(dissoc :completed-mill-event))
					game-finished? (check-for-end-game updated-game)]
			(if game-finished?
				(assoc updated-game :game-over-event true)
				(assoc updated-game :game-over-event nil)))
 	))