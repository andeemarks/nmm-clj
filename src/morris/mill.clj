(ns morris.mill
  (:require [loom.graph :as graph]
            [morris.board :as board]
            [morris.piece :as piece]))

(defn occupied-by-same-colour-pieces? [mill game-state]
	(let [nodes-in-mill (graph/nodes mill)
				first-node-piece ((first nodes-in-mill) game-state)
				second-node-piece ((second nodes-in-mill) game-state)
				third-node-piece ((last nodes-in-mill) game-state)]
		(piece/from-same-player? [first-node-piece second-node-piece third-node-piece])))

(defn locations-all-occupied? [mill game-state]
	(let [nodes-in-mill (graph/nodes mill)
				first-node (first nodes-in-mill)
				second-node (second nodes-in-mill)
				third-node (last nodes-in-mill)
				]
		(and
			(not (board/location-available? first-node game-state))
			(not (board/location-available? second-node game-state))
			(not (board/location-available? third-node game-state)))))

(defn check-for-completed-mill [mill game-state]
	(when
		(and
			(occupied-by-same-colour-pieces? mill game-state)
			(locations-all-occupied? mill game-state))
		(graph/nodes mill)))

(defn mill-contains-recent-move? [recent-move mill]
	(graph/has-node? mill recent-move))

(defn find-completed-mills [game-state recent-move]
	(let [relevant-mills (filter #(mill-contains-recent-move? recent-move %) board/mills)
				just-completed-mills (map #(check-for-completed-mill % game-state) relevant-mills) ]
		(remove nil? just-completed-mills)))
