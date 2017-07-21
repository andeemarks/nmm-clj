(ns morris.core
	(:require 
		[morris.board :as board]
		[taoensso.timbre :as log]
		[morris.mill :as mill]
		[clojure.string :as str]
		[morris.piece :as piece]
		))

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

(defn check-for-end-game [game]
	(let [white-piece-pool-size (count (:white-pieces game))
				black-piece-pool-size (count (:black-pieces game))
				white-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":white") (:game-state game)))
				black-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":black") (:game-state game)))
				insufficient-white-pieces? (< (+ white-pieces-on-board-count white-piece-pool-size) 3)
				insufficient-black-pieces? (< (+ black-pieces-on-board-count black-piece-pool-size) 3)]
		(or insufficient-white-pieces? insufficient-black-pieces?)))

(defn remove-piece [game location-containing-piece]
	(let [piece-to-remove (get (:game-state game) location-containing-piece)]
		(if (board/location-available? location-containing-piece (:game-state game))
			(throw (IllegalArgumentException. (str "Location " location-containing-piece " is not occupied")))
			(if (= (:current-player game) (piece/extract-colour piece-to-remove))
				(throw (IllegalArgumentException. (str (:current-player game) " cannot remove their own piece at " location-containing-piece)))
				(let [updated-game (-> game
														(update-in [:game-state] dissoc location-containing-piece)
														(dissoc :completed-mill-event))
							game-finished? (check-for-end-game updated-game)]
					(log/info "Removing piece from " location-containing-piece)
					(if game-finished?
						(assoc updated-game :game-over-event true)
						(assoc updated-game :game-over-event nil)))
		 	))))