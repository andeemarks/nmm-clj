(ns morris.board
	(:require 
		[loom.graph :refer :all]
		[morris.piece :as piece]
		[loom.attr :refer :all]
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
		; white pieces
		[:w1 :w2] [:w2 :w3] [:w3 :w4] [:w4 :w5] [:w5 :w6] [:w6 :w7] [:w7 :w8] [:w8 :w9]
		; black pieces
		[:bl1 :bl2] [:bl2 :bl3] [:bl3 :bl4] [:bl4 :bl5] [:bl5 :bl6] [:bl6 :bl7] [:bl7 :bl8] [:bl8 :bl9]
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
	      		(add-attr-to-nodes :fillcolor piece-colour [piece-id])
	      		(add-attr-to-nodes :labelfontcolor piece-colour [piece-id])
	      		(add-attr-to-nodes :shape "circle" [piece-id]))
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
			(add-attr-to-nodes :pos "-1,-3!" [:w1])
			(add-attr-to-nodes :pos "0,-3!" [:w2])
			(add-attr-to-nodes :pos "1,-3!" [:w3])
			(add-attr-to-nodes :pos "2,-3!" [:w4])
			(add-attr-to-nodes :pos "3,-3!" [:w5])
			(add-attr-to-nodes :pos "4,-3!" [:w6])
			(add-attr-to-nodes :pos "5,-3!" [:w7])
			(add-attr-to-nodes :pos "6,-3!" [:w8])
			(add-attr-to-nodes :pos "7,-3!" [:w9])
			(add-attr-to-nodes :shape "circle" [:w1 :w2 :w3 :w4 :w5 :w6 :w7 :w8 :w9])
			(add-attr-to-nodes :color "black" [:w1 :w2 :w3 :w4 :w5 :w6 :w7 :w8 :w9])
			(add-attr-to-nodes :fillcolor "white" [:w1 :w2 :w3 :w4 :w5 :w6 :w7 :w8 :w9])
			(add-attr-to-nodes :style "filled" [:w1 :w2 :w3 :w4 :w5 :w6 :w7 :w8 :w9])
			(add-attr-to-nodes :width "0.05" [:w1 :w2 :w3 :w4 :w5 :w6 :w7 :w8 :w9])
	))

(defn add-black-pieces [board black-pieces]
	(-> board
			(add-attr-to-nodes :pos "-1,-4!" [:bl1])
			(add-attr-to-nodes :pos "0,-4!" [:bl2])
			(add-attr-to-nodes :pos "1,-4!" [:bl3])
			(add-attr-to-nodes :pos "2,-4!" [:bl4])
			(add-attr-to-nodes :pos "3,-4!" [:bl5])
			(add-attr-to-nodes :pos "4,-4!" [:bl6])
			(add-attr-to-nodes :pos "5,-4!" [:bl7])
			(add-attr-to-nodes :pos "6,-4!" [:bl8])
			(add-attr-to-nodes :pos "7,-4!" [:bl9])
			(add-attr-to-nodes :shape "circle" [:bl1 :bl2 :bl3 :bl4 :bl5 :bl6 :bl7 :bl8 :bl9])
			(add-attr-to-nodes :color "black" [:bl1 :bl2 :bl3 :bl4 :bl5 :bl6 :bl7 :bl8 :bl9])
			(add-attr-to-nodes :fillcolor "black" [:bl1 :bl2 :bl3 :bl4 :bl5 :bl6 :bl7 :bl8 :bl9])
			(add-attr-to-nodes :style "filled" [:bl1 :bl2 :bl3 :bl4 :bl5 :bl6 :bl7 :bl8 :bl9])
			(add-attr-to-nodes :size "0.05" [:bl1 :bl2 :bl3 :bl4 :bl5 :bl6 :bl7 :bl8 :bl9])
	))

(defn layout [game]
	(-> (:board game)
			(add-common-layout)
			(add-position-hints)
			(add-white-pieces (:white-pieces game))
			(add-black-pieces (:black-pieces game))
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
	(has-node? (board) location))

(defn location-available? [location game-state]
	(nil? (location game-state)))

