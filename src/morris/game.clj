(ns morris.game
	(:require 
		[morris.board :as board]
		[morris.core :as core]
		[clojure.java.shell :as shell]))

(defn get-input [prompt]
  (println prompt)
  (read-line))

(defn- valid-move? [move game-state]
	(and 
		(board/location-available? move game-state)
		(board/location-exists? move)))

(defn- next-player [current-player]
	(if (= "white" current-player)
		"black"
		"white"))

(defn- process-player-move [game player]
  (loop [current-player player
  			move (keyword (get-input (str "[" current-player "] What is your move?")))]
    (if (valid-move? move (:game-state game))
    	(let [new-game-state (core/update-game game (str current-player "-1") move)]
    		new-game-state)
      (recur 
      	current-player
      	(keyword (get-input (str "[" current-player "] That is not a valid position - what is your move?")))))))

(defn- show-board [board-state round]
	(spit (str "target/board-" round ".dot") board-state)
	(spit (str "target/board-latest.dot")    board-state)
	(shell/sh "bash" "-c" "fdp target/board-latest.dot -Tsvg | display"))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [game (core/init-game) player "white" round 1]
		(show-board (board/show (:board game) (:game-state game)) round)
		(let [game-in-progress (process-player-move game player)]
	    (recur game-in-progress (next-player player) (inc round)))))