(ns morris.fe.game
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as str]
            [morris.common.board :as board]
            [morris.fe.api :as api]
            [morris.fe.input :as input]
            [morris.fe.output :as output]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]))

(log/merge-config! {:appenders {:spit (appenders/spit-appender {:fname "morris.log"})}})
(log/merge-config! {:appenders {:println nil}})

(defn find-pieces [game-state player]
	(keys (into '{} (filter #(str/starts-with? (name (val %)) player) (:pieces-on-board game-state)))))

(defn choose-player [game-state]
	(let [current-player (:current-player game-state)]
		(if (= "white" current-player)
			"black"
			"white")))

(defn choose-piece [game-state]
	(if (>= (count (:white-pieces game-state)) (count (:black-pieces game-state)))
		(first (:white-pieces game-state))
		(first (:black-pieces game-state))))

(defn- show [game-state]
	(let [board-state (output/show game-state)]
		(spit (str "target/game-latest.clj")   game-state)
		(spit (str "target/board-latest.dot")    board-state)
		(shell/sh "bash" "-c" "fdp target/board-latest.dot -Tsvg | display")))

(defn- save-game [game-state]
	(spit (str "target/game-latest.clj")   game-state))

(defmulti process-round (fn [mode game piece] mode))

(defmethod process-round "piece-movement" [mode game-state piece]
	(log/info "PIECE MOVEMENT for piece: " piece)
	(let [pieces-to-move (find-pieces game-state (:current-player game-state))]
	  (loop [move-components (input/for-player-move (:current-player game-state) (str " What is your move (from/to) " pieces-to-move "?"))]
	    (if (board/valid-move? (:current-player game-state) (:pieces-on-board game-state) (:origin move-components) (:destination move-components))
	    	(assoc (api/move-piece game-state (:origin move-components) (:destination move-components)) :mode mode)
	      (recur (input/for-player-move (:current-player game-state) (str " That is not a valid move - what is your move (from/to) " pieces-to-move "?")))))))

(defmethod process-round "piece-placement" [mode game-state piece]
	(log/info "PIECE PLACEMENT for piece: " piece)
  (loop [move (input/for-piece (:current-player game-state) " Where do you want to place this piece?" game-state)]
    (if (board/valid-placement? move (:pieces-on-board game-state))
    	(assoc (api/place-piece game-state piece move) :mode mode)
      (recur (input/for-piece (:current-player game-state) " That is not a valid position - where do you want to place this piece?" game-state)))))

(defmethod process-round "piece-removal" [mode game-state _]
	(log/info "PIECE REMOVAL by player: " (:current-player game-state))
	(let [pieces-to-remove (find-pieces game-state (choose-player game-state))]
	  (loop [location-to-remove (input/for-piece (:current-player game-state)  (str " Mill completed! Which piece do you want to remove " pieces-to-remove "?") game-state)]
	    (if (board/valid-removal? (:current-player game-state) location-to-remove (:pieces-on-board game-state))
	    	(assoc (api/remove-piece game-state location-to-remove) :mode mode)
	      (recur (input/for-piece (:current-player game-state) (str " That is not a valid position - which piece to remove " pieces-to-remove "?") game-state))))))

(defmethod process-round :game-over [mode game-state _]
	(log/info "GAME OVER for: " game-state)
	(println "Game over!"))

(def ^:const existing-game-config-file "resources/save-state.clj")

(defn- reload-saved-game [saved-game]
	(log/info "Found saved game at " saved-game)
	(let [saved-game (read-string (slurp existing-game-config-file))]
		(log/info saved-game)
		saved-game))

(defn- init-or-load-game []
	(if (.exists (io/as-file existing-game-config-file))
		(reload-saved-game existing-game-config-file)
		(api/init-game)))

(defn switch-player [game-state]
	(log/debug "Switching player from " (:current-player game-state))
	(assert (:current-player game-state) "Game has not recorded current player")
	(assoc game-state :current-player (choose-player game-state)))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [	game-state (init-or-load-game) 
					piece (choose-piece game-state) 
					mode (:mode game-state)]
		(show game-state)
		(log/info "Current piece: " piece " for player: " (:current-player game-state))
		(let [game-in-progress (process-round mode game-state piece)
					next-piece (choose-piece game-in-progress)]
			(save-game game-in-progress)
			(cond 
				(:completed-mill-event game-in-progress)
		    	(recur game-in-progress piece "piece-removal")
				(:game-over-event game-in-progress)
					(process-round :game-over game-in-progress nil)
				(not next-piece) ; next-piece has no remaining pieces
					(recur (switch-player game-in-progress) nil "piece-movement")
				:else
	    		(recur (switch-player game-in-progress) next-piece "piece-placement")))))
