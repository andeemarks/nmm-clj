(ns morris.core
	(:require 
		[loom.graph :refer :all]
		[morris.board :as board]
		[morris.mill :as mill]
		[morris.piece :as piece]
		[loom.io :refer :all]
		))

(defn init-game []
	{:board (board/board)
		:white-pieces (piece/make-white-pieces)
		:black-pieces (piece/make-black-pieces)
		:game-state nil})

(defn remove-piece-from-pool [piece-on-board white-pieces black-pieces]
	; (println piece-on-board white-pieces black-pieces)
	{:board (board/board)
		:white-pieces (remove #(= piece-on-board %) white-pieces)
		:black-pieces (remove #(= piece-on-board %) black-pieces)
		:game-state nil})

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

(defn remove-piece [game location-containing-piece]
	(if (board/location-available? location-containing-piece (:game-state game))
		(throw (IllegalArgumentException. (str "Location " location-containing-piece " is not occupied")))
		(let [new-game-state (dissoc (:game-state game) location-containing-piece)]
			(assoc game :game-state new-game-state))))