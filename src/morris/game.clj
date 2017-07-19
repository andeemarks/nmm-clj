(ns morris.game
	(:require 
		[morris.board :as board]
		[io.aviso.ansi :refer :all]
		[morris.core :as core]
		[morris.piece :as piece]
		[clojure.string :as str]
		[clojure.java.shell :as shell]))

(defn get-input [prompt]
  (println prompt)
  (read-line))

(def location-re "([A-Za-z][\\d])")
(def whitespace-re "\\s*")
(def move-re (str location-re whitespace-re "/" whitespace-re location-re))

(defn- location-to-move-component [components index]
	(let [component (nth components index)]
		(if component
			(keyword (str/lower-case component))
			nil)))

(defn move-components [move]
	(let [components (re-find (re-pattern move-re) move)
				origin (location-to-move-component components 1)
				destination (location-to-move-component components 2)]
		{:origin origin :destination destination}))

(defn valid-move? [move-components game-state]
	(board/valid-move? game-state (:origin move-components) (:destination move-components)))

(defn- valid-removal? [location-to-remove game-state]
	(not 
		(board/location-available? location-to-remove game-state)))

(defn- valid-placement? [move game-state]
	(and 
		(board/location-available? move game-state)
		(board/location-exists? move)))

(defn choose-piece [game]
	(if (>= (count (:white-pieces game)) (count (:black-pieces game)))
		(first (:white-pieces game))
		(first (:black-pieces game))))

(defn- show [board-state round]
	(spit (str "target/board-" round ".dot") board-state)
	(spit (str "target/board-latest.dot")    board-state)
	(shell/sh "bash" "-c" "fdp target/board-latest.dot -Tsvg | display"))

(defn- piece-label [piece game]
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str (piece/extract-colour piece) "-bg")))
				white-piece-pool-size (count (:white-pieces game))
				black-piece-pool-size (count (:black-pieces game))]
		(piece-colour-code (str bold-red-font " [" piece "] " white-piece-pool-size "/white " black-piece-pool-size "/black remaining " reset-font))))

(defn- player-label [player]
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str player "-bg")))]
		(piece-colour-code (str bold-red-font " [" player "] " reset-font))))

(defn- input-for-piece [piece prompt game]
	(keyword (get-input (str (piece-label piece game) prompt))))

(defn- input-for-player [player prompt]
	(get-input (str (player-label player) prompt)))

(defmulti process-round (fn [mode game piece] mode))

(defmethod process-round :piece-movement [mode game player]
  (loop [move (input-for-player player " What is your move (from/to)?")]
    (if (valid-move? (move-components move) (:game-state game))
    	(core/move-piece game (:origin (move-components move)) (:destination (move-components move)))
      (recur 
      	(input-for-player player " That is not a valid move - what is your move (from/to)?")))))

(defmethod process-round :piece-placement [mode game piece]
  (loop [move (input-for-piece piece " Where do you want to place this piece?" game)]
    (if (valid-placement? move (:game-state game))
    	(core/update-game game piece move)
      (recur 
      	(input-for-piece piece " That is not a valid position - where do you want to place this piece?" game)))))

(defmethod process-round :piece-removal [mode game piece]
  (loop [location-to-remove (input-for-piece piece  " Mill completed! Which piece do you want to remove?" game)]
    (if (valid-removal? location-to-remove (:game-state game))
    	(core/remove-piece game location-to-remove)
      (recur 
      	(input-for-piece piece " That is not a valid position - which piece to remove?" game)))))

(defmethod process-round :game-over [mode game piece]
	(println "Game over!"))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [	game (core/init-game) 
					piece (choose-piece game) 
					round 1 
					mode :piece-placement]
		(show (board/show game) round)
		(let [game-in-progress (process-round mode game piece)
					next-piece (choose-piece game-in-progress)]
			(cond 
				(not next-piece) ; next-piece has no remaining pieces
					(recur game-in-progress "white" round :piece-movement)
				(:completed-mill-event game-in-progress)
		    	(recur game-in-progress piece round :piece-removal)
				(:game-over-event game-in-progress)
					(process-round :game-over)
				:else
	    		(recur game-in-progress next-piece (inc round) :piece-placement)))))
