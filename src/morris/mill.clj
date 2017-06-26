(ns morris.mill
	(:require 
		[morris.board :as board]
		[morris.piece :as piece]
		[loom.graph :as graph]
		))

(defn check-for-completed-mill [mill game-state]
	(let [nodes-in-mill (graph/nodes mill)
				first-node (first nodes-in-mill)
				second-node (second nodes-in-mill)
				third-node (last nodes-in-mill)]
		(not 
			(or 
				(board/location-available? first-node game-state)
				(board/location-available? second-node game-state)
				(board/location-available? third-node game-state)))))

(defn mill-contains-recent-move? [recent-move mill]
	(graph/has-node? mill recent-move))

(defn check-for-completed-mills [game-state recent-move]
	(let [relevant-mills (filter #(mill-contains-recent-move? recent-move %) board/mills)
				just-completed-mills (map #(check-for-completed-mill % game-state) relevant-mills) ]
		(> (count (filter true? just-completed-mills)) 0)))
