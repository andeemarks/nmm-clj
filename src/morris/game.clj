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
		(core/location-available? move game-state)
		(board/location-exists? move)))

(defn- next-player [current-player]
	(if (= "white" current-player)
		"black"
		"white"))

(defn- process-player-move [game player]
  (loop [current-player player
  			move (keyword (get-input (str "[" current-player "] What is your move?")))]
    (if (valid-move? move (:game-state game))
      (core/update-game game (str current-player "-1") move)
      (recur 
      	current-player
      	(keyword (get-input (str "[" current-player "] That is not a valid position - what is your move?")))))))

(defn- show-board [board]
	(spit (str "target/board-" round ".dot") board)
	(spit (str "target/board-latest.dot")    board)
	(shell/sh "bash" "-c" "fdp target/board-latest.dot -Tsvg | display"))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [game (core/init-game) player "white" round 1]
		(let [game-in-progress (process-player-move game player)
					board (board/show (:board game-in-progress) (:game-state game-in-progress))]
			(show-board board)
	    (recur game-in-progress (next-player player) (inc round)))))