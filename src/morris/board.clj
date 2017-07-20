(ns morris.board
	(:require 
		[loom.graph :refer :all]
		[taoensso.timbre :as log]
		[morris.piece :as piece]
		[loom.attr :refer :all]
		[loom.alg :refer :all]
		[loom.io :refer :all]
		))

(defn make-location [id] id)

(defn connect-from-to [from to]
	[(make-location from) (make-location to)])

(defn board [] 
	(graph 
		; outer square
		(connect-from-to :a1 :d1) (connect-from-to :d1 :g1) (connect-from-to :g1 :g4) (connect-from-to :g4 :g7) (connect-from-to :g7 :d7) (connect-from-to :d7 :a7) (connect-from-to :a7 :a4) (connect-from-to :a4 :a1)
		; inner square
		(connect-from-to :c3 :d3) (connect-from-to :c3 :c4) (connect-from-to :d3 :e3) (connect-from-to :e3 :e4) (connect-from-to :c4 :c5) (connect-from-to :e4 :e5) (connect-from-to :c5 :d5) (connect-from-to :d5 :e5)
		; middle square
		(connect-from-to :b2 :d2) (connect-from-to :d2 :f2) (connect-from-to :b2 :b4) (connect-from-to :b4 :b6) (connect-from-to :b6 :d6) (connect-from-to :d6 :f6) (connect-from-to :f6 :f4) (connect-from-to :f4 :f2)
		; connectors
		(connect-from-to :b4 :c4) (connect-from-to :f4 :e4) (connect-from-to :d2 :d3) (connect-from-to :d6 :d5) (connect-from-to :d1 :d2) (connect-from-to :a4 :b4) (connect-from-to :d7 :d6) (connect-from-to :g4 :f4)
		))

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

(defn add-white-pieces [board white-pieces]
	(-> board
			(add-nodes :white-1 :white-2 :white-3 :white-4 :white-5 :white-6 :white-7 :white-8 :white-9)
			(add-path :white-1 :white-2 :white-3 :white-4 :white-5 :white-6 :white-7 :white-8 :white-9)
			(add-attr-to-nodes :pos "-1,-3!" [:white-1])
			(add-attr-to-nodes :pos "0,-3!" [:white-2])
			(add-attr-to-nodes :pos "1,-3!" [:white-3])
			(add-attr-to-nodes :pos "2,-3!" [:white-4])
			(add-attr-to-nodes :pos "3,-3!" [:white-5])
			(add-attr-to-nodes :pos "4,-3!" [:white-6])
			(add-attr-to-nodes :pos "5,-3!" [:white-7])
			(add-attr-to-nodes :pos "6,-3!" [:white-8])
			(add-attr-to-nodes :pos "7,-3!" [:white-9])
			(add-attr-to-nodes :shape "circle" white-pieces)
			(add-attr-to-nodes :color "black" white-pieces)
			(add-attr-to-nodes :fillcolor "white" white-pieces)
			(add-attr-to-nodes :style "filled" white-pieces)
			(add-attr-to-nodes :width "0.05" white-pieces)
	))

(defn add-black-pieces [board black-pieces]
	(-> board
			(add-nodes :black-1 :black-2 :black-3 :black-4 :black-5 :black-6 :black-7 :black-8 :black-9)
			(add-path :black-1 :black-2 :black-3 :black-4 :black-5 :black-6 :black-7 :black-8 :black-9)
			(add-attr-to-nodes :pos "-1,-4!" [:black-1])
			(add-attr-to-nodes :pos "0,-4!" [:black-2])
			(add-attr-to-nodes :pos "1,-4!" [:black-3])
			(add-attr-to-nodes :pos "2,-4!" [:black-4])
			(add-attr-to-nodes :pos "3,-4!" [:black-5])
			(add-attr-to-nodes :pos "4,-4!" [:black-6])
			(add-attr-to-nodes :pos "5,-4!" [:black-7])
			(add-attr-to-nodes :pos "6,-4!" [:black-8])
			(add-attr-to-nodes :pos "7,-4!" [:black-9])
			(add-attr-to-nodes :shape "circle" black-pieces)
			(add-attr-to-nodes :color "white" black-pieces)
			(add-attr-to-nodes :fillcolor "black" black-pieces)
			(add-attr-to-nodes :style "filled" black-pieces)
			(add-attr-to-nodes :size "0.05" black-pieces)
	))

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
	(-> (board)
			(add-common-layout)
			(add-position-hints)
			(add-completed-mills (:completed-mill-event game))
			; (add-white-pieces (:white-pieces game))
			; (add-black-pieces (:black-pieces game))
			(add-pieces (:game-state game))))

(defn show [game]
	(dot-str (layout game)))

(defn make-mill [loc1 loc2 loc3]
	(subgraph (board) [(make-location loc1) (make-location loc2) (make-location loc3)]))

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

(defn location-exists? [location]
	(let [result (has-node? (board) location)]
		(log/debug "location-exists? for " location "=> " result)
		result))

(defn location-available? [location game-state]
	(let [result (nil? (location game-state))]
		(log/debug "location-available? for " location " with " game-state "=> " result)
		result))

(defn neighbour? [board loc1 loc2]
	(let [result (= 2 (count (bf-path board loc1 loc2)))]
		(log/debug "neighbour? of " loc1 " and " loc2 "=> " result)
		result))

(defn valid-move? [game-state origin destination]
	(and 
		(location-exists? origin)
		(location-available? destination game-state)
		(neighbour? (board) origin destination)
		(not (location-available? origin game-state))))