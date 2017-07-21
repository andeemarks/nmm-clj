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

(defn move-piece [game origin destination]
	(let [piece-to-move (origin (:game-state game))]
		(if (board/valid-move? (:current-player game) (:game-state game) origin destination)
			(let [new-game (update-game-for-move game piece-to-move origin destination)
						mill-completed? (mill/find-completed-mills (:game-state new-game) destination)]
				(log/info "Moving " piece-to-move " from " origin " to " destination)
				(if mill-completed?
					(assoc new-game :completed-mill-event (first mill-completed?))
					(assoc new-game :completed-mill-event nil)))
			(throw (IllegalStateException. (str "Cannot move " piece-to-move " from " origin " to " destination))))))

(defn place-piece [game piece destination]
	(let [current-game-state (:game-state game)]

		(if (board/location-exists? destination)				
			(if (board/location-available? destination current-game-state)
				(let [new-game-state (merge current-game-state {destination piece})
							game-with-updated-player-pools (remove-piece-from-pool game piece)
							new-game (assoc game-with-updated-player-pools :game-state new-game-state)
							mill-completed? (mill/find-completed-mills new-game-state destination)]
					(log/info "Placing " piece " on " destination)
					(if mill-completed?
						(assoc new-game :completed-mill-event (first mill-completed?))
						(assoc new-game :completed-mill-event nil)))
				(throw (IllegalStateException. (str "Location " destination " is already occupied"))))
			(throw (IllegalArgumentException. (str "Location " destination " does not exist on board"))))))

(defn remove-piece [game location-containing-piece]
	(if (board/valid-removal? (:current-player game) location-containing-piece (:game-state game))
		(let [updated-game (-> game
												(update-in [:game-state] dissoc location-containing-piece)
												(dissoc :completed-mill-event))
					game-finished? (board/end-game? (:white-pieces updated-game) (:black-pieces updated-game) (:game-state updated-game))]
			(log/info "Removing piece from " location-containing-piece)
			(if game-finished?
				(assoc updated-game :game-over-event true)
				(assoc updated-game :game-over-event nil))) 
		(throw (IllegalArgumentException. (str "Location " location-containing-piece " cannot be removed by " (:current-player game))))))