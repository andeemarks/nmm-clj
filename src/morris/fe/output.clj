(ns morris.fe.output
  (:require [loom.alg :refer :all]
            [loom.attr :refer :all]
            [loom.graph :refer :all]
            [loom.io :refer :all]
            [morris.be.piece :as piece]
            [morris.common.board :as board]
            [taoensso.timbre :as log]))

(defn add-pieces [board game-state]
  (loop [board-with-pieces board pieces-on-board (keys game-state)]
   	(if (nil? (first pieces-on-board))
      board-with-pieces
      (let [piece-id (first pieces-on-board)
      			piece-to-show (piece-id game-state)
      			piece-colour (piece/extract-colour piece-to-show)]
	      (recur 
	      	(-> board-with-pieces
	      		(add-attr piece-id :fillcolor piece-colour )
	      		(add-attr piece-id :fontcolor "#ff0000" )
	      		(add-attr piece-id :shape "circle" ))
	    		(rest pieces-on-board))))))

(defn- add-common-layout [board]
	(let [all-nodes (nodes board)]
		(-> board
				(add-attr-to-nodes :width 0.25 all-nodes)
				(add-attr-to-nodes :shape "circle" all-nodes)
				(add-attr-to-nodes :style "filled" all-nodes)
				(add-attr-to-nodes :color "gray" all-nodes))))

(defn- add-position-hints [board]
	(-> board
			(add-attr-to-nodes :pos "2,2!" [:c3])
			(add-attr-to-nodes :pos "3,2!" [:d3])
			(add-attr-to-nodes :pos "4,2!" [:e3])
			(add-attr-to-nodes :pos "2,1!" [:c4])
			(add-attr-to-nodes :pos "4,1!" [:e4])
			(add-attr-to-nodes :pos "2,0!" [:c5])
			(add-attr-to-nodes :pos "3,0!" [:d5])
			(add-attr-to-nodes :pos "4,0!" [:e5])
			(add-attr-to-nodes :pos "1,3!" [:b2])
			(add-attr-to-nodes :pos "3,3!" [:d2])
			(add-attr-to-nodes :pos "5,3!" [:f2])
			(add-attr-to-nodes :pos "1,-1!" [:b6])
			(add-attr-to-nodes :pos "3,-1!" [:d6])
			(add-attr-to-nodes :pos "5,-1!" [:f6])
			(add-attr-to-nodes :pos "1,1!" [:b4])
			(add-attr-to-nodes :pos "5,1!" [:f4])
			(add-attr-to-nodes :pos "0, 4!" [:a1])
			(add-attr-to-nodes :pos "3, 4!" [:d1])
			(add-attr-to-nodes :pos "6, 4!" [:g1])
			(add-attr-to-nodes :pos "0, -2!" [:a7])
			(add-attr-to-nodes :pos "3, -2!" [:d7])
			(add-attr-to-nodes :pos "6, -2!" [:g7])))

(defn hilite-completed-mill [board mill]
	(-> board
		(hilite-path (bf-path board (first mill) (last mill)))
		(hilite-path (bf-path board (first mill) (second mill)))))

(defn add-completed-mills [board completed-mill-event]
	(if completed-mill-event
		(let []
			(hilite-completed-mill board completed-mill-event))
		board))

(defn layout [game]
	(-> (board/board)
			(add-common-layout)
			(add-position-hints)
			(add-completed-mills (:completed-mill-event game))
			(add-pieces (:pieces-on-board game))))

(defn show [game]
	(dot-str (layout game)))
