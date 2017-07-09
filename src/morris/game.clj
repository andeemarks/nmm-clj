(ns morris.game
	(:require 
		[morris.board :as board]
		[io.aviso.ansi :refer :all]
		[morris.core :as core]
		[morris.piece :as piece]
		[clojure.java.shell :as shell]))

(defn get-input [prompt]
  (println prompt)
  (read-line))

(defn- valid-removal? [location-to-remove game-state]
	(not 
		(board/location-available? location-to-remove game-state)))

(defn- valid-placement? [move game-state]
	(and 
		(board/location-available? move game-state)
		(board/location-exists? move)))

(defn choose-piece [game]
	(if 
		(>= 
			(count (:white-pieces game)) 
			(count (:black-pieces game)))
		(first (:white-pieces game))
		(first (:black-pieces game))))

(defn- show [board-state round]
	(spit (str "target/board-" round ".dot") board-state)
	(spit (str "target/board-latest.dot")    board-state)
	(shell/sh "bash" "-c" "fdp target/board-latest.dot -Tsvg | display"))

(defn- piece-label [piece]
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str (piece/extract-colour piece) "-bg")))]
		(piece-colour-code (str bold-red-font " [" piece "] " reset-font))))

(defmulti process-round (fn [mode game piece] mode))

(defmethod process-round :piece-placement [mode game piece]
  (loop [move (keyword (get-input (str (piece-label piece) " Where do you want to place this piece?")))]
    (if (valid-placement? move (:game-state game))
    	(core/update-game game piece move)
      (recur 
      	(keyword (get-input (str (piece-label piece) " That is not a valid position - where do you want to place this piece?")))))))

(defmethod process-round :piece-removal [mode game piece]
  (loop [location-to-remove (keyword (get-input (str (piece-label piece) " Mill completed! Which piece do you want to remove?")))]
    (if (valid-removal? location-to-remove (:game-state game))
    	(core/remove-piece game location-to-remove)
      (recur 
      	(keyword (get-input (str (piece-label piece) " That is not a valid position - which piece to remove?")))))))

(defmethod process-round :game-over [mode game piece]
	(println "Game over!"))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [	game (core/init-game) 
					piece (choose-piece game) 
					round 1 
					mode :piece-placement]
		(show (board/show game) round)
		(let [game-in-progress (process-round mode game piece)]
			(cond 
				(:completed-mill-event game-in-progress)
		    	(recur game-in-progress piece round :piece-removal)
				(:game-over-event game-in-progress)
					(process-round :game-over)
				:else
	    		(recur game-in-progress (choose-piece game-in-progress) (inc round) :piece-placement)))))
