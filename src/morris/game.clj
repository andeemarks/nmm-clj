(ns morris.game
	(:require 
		[morris.board :as board]
		[morris.core :as core]
		[clojure.java.shell :as shell]))

(defn get-input [prompt]
  (println prompt)
  (read-line))

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

(defn- process-player-piece-placement [game piece]
  (loop [current-piece piece
  			move (keyword (get-input (str "[" current-piece "] What is your move?")))]
    (if (valid-placement? move (:game-state game))
    	(let [new-game-state (core/update-game game current-piece move)]
    		new-game-state)
      (recur 
      	current-piece
      	(keyword (get-input (str "[" current-piece "] That is not a valid position - what is your move?")))))))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [game (core/init-game) piece (choose-piece game) round 1 mode :piece-placement]
		(show (board/show game) round)
		(let [game-in-progress (process-player-piece-placement game piece)]
			(if (:mill-completed-event game-in-progress)
		    (recur game-in-progress piece round :piece-removal)
	    	(recur game-in-progress (choose-piece game-in-progress) (inc round) :piece-placement)))))
