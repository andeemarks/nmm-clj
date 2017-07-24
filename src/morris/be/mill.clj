(ns morris.be.mill
  (:require [loom.graph :as graph]
            [loom.graph :refer :all]
            [morris.common.board :as board]
            [morris.be.piece :as piece]))

(defn make-mill [loc1 loc2 loc3]
	(subgraph (board/board) [loc1 loc2 loc3]))

(def mills [
	; horizontal mills
	(make-mill :a1 :d1 :g1)
	(make-mill :b2 :d2 :f2)
	(make-mill :c3 :d3 :e3)
	(make-mill :a4 :b4 :c4)
	(make-mill :e4 :f4 :g4)
	(make-mill :c5 :d5 :e5)
	(make-mill :b6 :d6 :f6)
	(make-mill :a7 :d7 :g7)
	; vertical mills
	(make-mill :a1 :a4 :a7)
	(make-mill :b2 :b4 :b6)
	(make-mill :c3 :c4 :c5)
	(make-mill :d1 :d2 :d3)
	(make-mill :d5 :d6 :d7)
	(make-mill :e3 :e4 :e5)
	(make-mill :f2 :f4 :f6)
	(make-mill :g1 :g4 :g7)
	])

(defn occupied-by-same-colour-pieces? [mill pieces-on-board]
	(let [nodes-in-mill (graph/nodes mill)
				first-node-piece ((first nodes-in-mill) pieces-on-board)
				second-node-piece ((second nodes-in-mill) pieces-on-board)
				third-node-piece ((last nodes-in-mill) pieces-on-board)]
		(piece/from-same-player? [first-node-piece second-node-piece third-node-piece])))

(defn locations-all-occupied? [mill pieces-on-board]
	(let [nodes-in-mill (graph/nodes mill)
				first-node (first nodes-in-mill)
				second-node (second nodes-in-mill)
				third-node (last nodes-in-mill)
				]
		(not (or
			(board/location-available? first-node pieces-on-board)
			(board/location-available? second-node pieces-on-board)
			(board/location-available? third-node pieces-on-board)))))

(defn check-for-completed-mill [mill pieces-on-board]
	(when
		(and
			(occupied-by-same-colour-pieces? mill pieces-on-board)
			(locations-all-occupied? mill pieces-on-board))
		(graph/nodes mill)))

(defn mill-contains-recent-move? [recent-move mill]
	(graph/has-node? mill recent-move))

(defn find-completed-mills [pieces-on-board recent-move]
	(let [relevant-mills (filter #(mill-contains-recent-move? recent-move %) mills)
				just-completed-mills (map #(check-for-completed-mill % pieces-on-board) relevant-mills) ]
		(remove nil? just-completed-mills)))
