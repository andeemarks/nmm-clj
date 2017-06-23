(ns morris.core
	(:require 
		[loom.graph :refer :all]
		[morris.board :as board]
		[morris.piece :as piece]
		[loom.io :refer :all]
		))

(defn init-game []
	{:board (board/board)
		:white-pieces (piece/make-white-pieces)
		:black-pieces (piece/make-black-pieces)
		:game-state nil})

(defn location-available? [location game-state]
	; (println (str "Checking for availability of " location " in " game-state))
	(nil? (location game-state)))

(defn check-for-completed-mill [mill game-state]
	(let [nodes-in-mill (nodes mill)
				first-node (first nodes-in-mill)
				second-node (second nodes-in-mill)
				third-node (last nodes-in-mill)]
		(not (or (location-available? first-node game-state)
				(location-available? second-node game-state)
				(location-available? third-node game-state)))))

(defn check-for-completed-mills [game-state]
	(let [completed-mills (map #(check-for-completed-mill % game-state) board/mills)]
		(> (count (filter true? completed-mills)) 0)))

(defn update-game [game piece destination]
	(let [current-game-state (:game-state game)
				destination-available? (location-available? destination current-game-state)]
				
		(if destination-available?
			(let [new-game-state (merge current-game-state {destination piece})
						new-game (assoc game :game-state new-game-state)
						mill-completed? (check-for-completed-mills new-game-state)]
				(if mill-completed?
					(assoc new-game :event "mill completed")
					new-game))
			(throw (IllegalStateException. (str "Location " destination " is already occupied"))))))
