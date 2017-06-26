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
	(nil? (location game-state)))

(defn check-for-completed-mill [mill game-state]
	(let [nodes-in-mill (nodes mill)
				first-node (first nodes-in-mill)
				second-node (second nodes-in-mill)
				third-node (last nodes-in-mill)]
		(not (or (location-available? first-node game-state)
				(location-available? second-node game-state)
				(location-available? third-node game-state)))))

(defn mill-contains-recent-move? [recent-move mill]
	(has-node? mill recent-move))

(defn check-for-completed-mills [game-state recent-move]
	(let [relevant-mills (filter #(mill-contains-recent-move? recent-move %) board/mills)
				just-completed-mills (map #(check-for-completed-mill % game-state) relevant-mills)
				]
		(> (count (filter true? just-completed-mills)) 0)))

(defn update-game [game piece destination]
	(let [current-game-state (:game-state game)]

		(if (board/location-exists? destination)				
			(if (location-available? destination current-game-state)
				(let [new-game-state (merge current-game-state {destination piece})
							new-game (assoc game :game-state new-game-state)
							mill-completed? (check-for-completed-mills new-game-state destination)]
					(if mill-completed?
						(assoc new-game :event "mill completed")
						(assoc new-game :event nil)))
				(throw (IllegalStateException. (str "Location " destination " is already occupied"))))
			(throw (IllegalArgumentException. (str "Location " destination " does not exist on board"))))))
