(ns morris.core
  (:require [morris.board :as board]
            [morris.mill :as mill]
            [morris.piece :as piece]
            [taoensso.timbre :as log]))

(defn init-game []
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

(defn- handle-end-game-event [game]
	(let [game-finished? (board/end-game? (:white-pieces game) (:black-pieces game) (:game-state game))]
		(if game-finished?
			(assoc game :game-over-event true)
			(assoc game :game-over-event nil))))

(defn move-piece [game origin destination]
	(let [piece-to-move (origin (:game-state game))]
		(if (board/valid-move? (:current-player game) (:game-state game) origin destination)
			(let [new-game (update-game-for-move game piece-to-move origin destination)]
				(log/info "Moving " piece-to-move " from " origin " to " destination)
				(handle-mill-completion-event new-game destination))
			(throw (IllegalArgumentException. (str "Cannot move " piece-to-move " from " origin " to " destination))))))

(defn place-piece [game piece destination]
	(let [current-game-state (:game-state game)]
		(if (board/valid-placement? destination current-game-state)				
			(let [new-game-state (merge current-game-state {destination piece})
						game-with-updated-player-pools (remove-piece-from-pool game piece)
						new-game (assoc game-with-updated-player-pools :game-state new-game-state)]
				(log/info "Placing " piece " on " destination)
				(handle-mill-completion-event new-game destination))
			(throw (IllegalArgumentException. (str "Piece " piece " cannot be placed on location " destination))))))

(defn remove-piece [game location-containing-piece]
	(if (board/valid-removal? (:current-player game) location-containing-piece (:game-state game))
		(let [updated-game (-> game
												(update-in [:game-state] dissoc location-containing-piece)
												(dissoc :completed-mill-event))]
			(log/info "Removing piece from " location-containing-piece)
			(handle-end-game-event updated-game))
		(throw (IllegalArgumentException. (str "Location " location-containing-piece " cannot be removed by " (:current-player game))))))