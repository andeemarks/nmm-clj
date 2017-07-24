(ns morris.be.core
  (:require [morris.common.board :as board]
            [morris.be.mill :as mill]
            [morris.be.piece :as piece]
            [schema.core :as s]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(def Piece s/Keyword)
(def Location s/Keyword)
(def PiecesOnBoard {Location Piece})

(s/defschema GameState {
	:current-player		(s/enum "white" "black")
  :white-pieces  		[Piece]
  :black-pieces  		[Piece]
  :pieces-on-board 	(s/maybe PiecesOnBoard)
  :mode 						(s/enum :piece-removal :piece-movement :piece-placement :game-over) 
  (s/optional-key :completed-mill-event) s/Any
  (s/optional-key :game-over-event) s/Any})

(s/set-fn-validation! true)

(s/defn init-game :- GameState []
	(log/info "*** New game ***")
	{:mode :piece-placement
		:current-player "white"
		:white-pieces (piece/make-white-pieces)
		:black-pieces (piece/make-black-pieces)
		:pieces-on-board '{}})

(s/defn remove-piece-from-pool :- GameState [game-state :- GameState piece-on-board :- Piece]
	(-> game-state
		(assoc :white-pieces (remove #(= piece-on-board %) (:white-pieces game-state)))
		(assoc :black-pieces (remove #(= piece-on-board %) (:black-pieces game-state)))))

(s/defn update-game-for-move :- GameState [game-state :- GameState piece-to-move :- Piece origin :- Location destination :- Location]
	(-> game-state
		(assoc-in [:pieces-on-board destination] piece-to-move)
		(update-in [:pieces-on-board] dissoc origin)))

(s/defn handle-mill-completion-event :- GameState [game-state :- GameState destination :- Location]
	(let [completed-mills (mill/find-completed-mills (:pieces-on-board game-state) destination)]
		(if completed-mills
			(assoc game-state :completed-mill-event (first completed-mills))
			(assoc game-state :completed-mill-event nil))))

(s/defn end-game? :- Boolean [white-pieces black-pieces pieces-on-board :- PiecesOnBoard]
	(let [white-piece-pool-size (count white-pieces)
				black-piece-pool-size (count black-pieces)
				white-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":white") pieces-on-board))
				black-pieces-on-board-count (count (filter #(str/starts-with? (val %) ":black") pieces-on-board))
				insufficient-white-pieces? (< (+ white-pieces-on-board-count white-piece-pool-size) 3)
				insufficient-black-pieces? (< (+ black-pieces-on-board-count black-piece-pool-size) 3)]
		(or insufficient-white-pieces? insufficient-black-pieces?)))

(s/defn handle-end-game-event :- GameState [game-state :- GameState]
	(let [game-finished? (end-game? (:white-pieces game-state) (:black-pieces game-state) (:pieces-on-board game-state))]
		(if game-finished?
			(assoc game-state :game-over-event true)
			(assoc game-state :game-over-event nil))))

(s/defn move-piece :- GameState [game-state :- GameState origin :- Location destination :- Location]
	(let [piece-to-move (origin (:pieces-on-board game-state))]
		(log/info "Moving " piece-to-move " from " origin " to " destination)
		(if (board/valid-move? (:current-player game-state) (:pieces-on-board game-state) origin destination)
			(-> game-state
				(update-game-for-move piece-to-move origin destination)
				(handle-mill-completion-event destination))
			(throw (IllegalArgumentException. (str "Cannot move " piece-to-move " from " origin " to " destination))))))

(defn- add-piece-to-game [game-state destination piece] (update-in game-state [:pieces-on-board] merge {destination piece}))

(s/defn place-piece :- GameState [game-state :- GameState piece :- Piece destination :- Location]
	(log/info "Attempting to place " piece " on " destination)
	(if (board/valid-placement? destination (:pieces-on-board game-state))				
		(-> game-state 
			(add-piece-to-game destination piece)
			(remove-piece-from-pool piece)
			(handle-mill-completion-event destination))
		(throw (IllegalArgumentException. (str "Piece " piece " cannot be placed on location " destination)))))

(defn- remove-piece-from-game [game-state piece] (update-in game-state [:pieces-on-board] dissoc piece))
(defn- clear-events [game-state] (dissoc game-state :completed-mill-event))

(s/defn remove-piece :- GameState [game-state :- GameState location-containing-piece :- Location]
	(log/info "Attempting to remove piece from " location-containing-piece)
	(if (board/valid-removal? (:current-player game-state) location-containing-piece (:pieces-on-board game-state))
		(-> game-state
			(remove-piece-from-game location-containing-piece)
			(clear-events)
			(handle-end-game-event))
		(throw (IllegalArgumentException. (str "Location " location-containing-piece " cannot be removed by " (:current-player game-state))))))