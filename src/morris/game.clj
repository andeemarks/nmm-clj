(ns morris.game
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as str]
            [io.aviso.ansi :refer :all]
            [morris.board :as board]
            [morris.core :as core]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]))

(defn get-input [prompt]
  (println prompt)
  (read-line))

(def ^:const location-re "([A-Za-z][\\d])")
(def ^:const whitespace-re "\\s*")
(def ^:const move-re (str location-re whitespace-re "/" whitespace-re location-re))

(defn- location-to-move-component [components index]
	(let [component (nth components index)]
		(when component
			(keyword (str/lower-case component)))))

(defn move-components [move]
	(let [components (re-find (re-pattern move-re) move)
				origin (location-to-move-component components 1)
				destination (location-to-move-component components 2)]
		{:origin origin :destination destination}))

(defn find-pieces [game player]
	(keys (into '{} (filter #(str/starts-with? (name (val %)) player) (:game-state game)))))

(defn choose-player [game]
	(let [current-player (:current-player game)]
		(if (= "white" current-player)
			"black"
			"white")))

(defn choose-piece [game]
	(if (>= (count (:white-pieces game)) (count (:black-pieces game)))
		(first (:white-pieces game))
		(first (:black-pieces game))))

(defn- show [game]
	(let [board-state (board/show game)]
		(spit (str "target/game-latest.clj")   game)
		(spit (str "target/board-latest.dot")    board-state)
		(shell/sh "bash" "-c" "fdp target/board-latest.dot -Tsvg | display")))

(defn- save-game [game]
	(spit (str "target/game-latest.clj")   game))

(defn- piece-label [player game]
	(assert player "Attempting to build label for nil player!!!")
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str player "-bg")))
				white-piece-pool-size (count (:white-pieces game))
				black-piece-pool-size (count (:black-pieces game))]
		(piece-colour-code (str bold-red-font " [" player "] " white-piece-pool-size "/white " black-piece-pool-size "/black remaining " reset-font))))

(defn- player-label [player]
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str player "-bg")))]
		(piece-colour-code (str bold-red-font " [" player "] " reset-font))))

(defn- input-for-piece [player prompt game]
	(keyword (get-input (str (piece-label player game) prompt))))

(defn- input-for-player [player prompt]
	(get-input (str (player-label player) prompt)))

(defmulti process-round (fn [mode game piece] mode))

(defmethod process-round :piece-movement [mode game piece]
	(log/info "PIECE MOVEMENT for piece: " piece)
	(let [pieces-to-move (find-pieces game (:current-player game))]
	  (loop [move (input-for-player (:current-player game) (str " What is your move (from/to) " pieces-to-move "?"))]
	  	(let [move-components (move-components move)]
		    (if (board/valid-move? (:current-player game) (:game-state game) (:origin move-components) (:destination move-components))
		    	(assoc (core/move-piece game (:origin move-components) (:destination move-components)) :mode mode)
		      (recur 
		      	(input-for-player (:current-player game) (str " That is not a valid move - what is your move (from/to) " pieces-to-move "?"))))))))

(defmethod process-round :piece-placement [mode game piece]
	(log/info "PIECE PLACEMENT for piece: " piece)
  (loop [move (input-for-piece (:current-player game) " Where do you want to place this piece?" game)]
    (if (board/valid-placement? move (:game-state game))
    	(assoc (core/place-piece game piece move) :mode mode)
      (recur 
      	(input-for-piece (:current-player game) " That is not a valid position - where do you want to place this piece?" game)))))

(defmethod process-round :piece-removal [mode game piece]
	(log/info "PIECE REMOVAL by player: " (:current-player game))
	(let [pieces-to-remove (find-pieces game (choose-player game))]
	  (loop [location-to-remove (input-for-piece (:current-player game)  (str " Mill completed! Which piece do you want to remove " pieces-to-remove "?") game)]
	    (if (board/valid-removal? (:current-player game) location-to-remove (:game-state game))
	    	(assoc (core/remove-piece game location-to-remove) :mode mode)
	      (recur 
	      	(input-for-piece (:current-player game) (str " That is not a valid position - which piece to remove " pieces-to-remove "?") game))))))

(defmethod process-round :game-over [mode game piece]
	(log/info "GAME OVER for: " game)
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
		(core/init-game)))

(defn switch-player [game]
	(log/debug "Switching player from " (:current-player game))
	(assert (:current-player game) "Game has not recorded current player")
	(assoc game :current-player (choose-player game)))

(log/merge-config! {:appenders {:spit (appenders/spit-appender {:fname "morris.log"})}})
(log/merge-config! {:appenders {:println nil}})

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(log/info "*** New game ***")
	(loop [	game (init-or-load-game) 
					piece (choose-piece game) 
					mode (:mode game)]
		(show game)
		(log/info "Current piece: " piece " for player: " (:current-player game))
		(let [game-in-progress (process-round mode game piece)
					next-piece (choose-piece game-in-progress)]
			(save-game game-in-progress)
			(cond 
				(:completed-mill-event game-in-progress)
		    	(recur game-in-progress piece :piece-removal)
				(:game-over-event game-in-progress)
					(process-round :game-over game-in-progress nil nil)
				(not next-piece) ; next-piece has no remaining pieces
					(recur (switch-player game-in-progress) nil :piece-movement)
				:else
	    		(recur (switch-player game-in-progress) next-piece :piece-placement)))))
