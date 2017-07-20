(ns morris.game
	(:require 
		[morris.board :as board]
		[io.aviso.ansi :refer :all]
		[morris.core :as core]
		[morris.piece :as piece]
		[clojure.string :as str]
		[clojure.pprint :as pp]
		[clojure.java.io :as io]
		[clojure.java.shell :as shell]))

(defn get-input [prompt]
  (println prompt)
  (read-line))

(def ^:const location-re "([A-Za-z][\\d])")
(def ^:const whitespace-re "\\s*")
(def ^:const move-re (str location-re whitespace-re "/" whitespace-re location-re))

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

(defn find-pieces [game player]
	(keys (into '{} (filter #(str/starts-with? (name (val %)) player) (:game-state game)))))

(defn valid-move? [move-components game-state]
	(board/valid-move? game-state (:origin move-components) (:destination move-components)))

(defn- valid-removal? [location-to-remove game-state]
	(not 
		(board/location-available? location-to-remove game-state)))

(defn- valid-placement? [move game-state]
	(and 
		(board/location-available? move game-state)
		(board/location-exists? move)))

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

(defn- piece-label [player game]
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

(defmulti process-round (fn [mode game piece player] mode))

(defmethod process-round :piece-movement [mode game piece player]
  (loop [move (input-for-player player (str " What is your move (from/to) " (find-pieces game player) "?"))]
    (if (valid-move? (move-components move) (:game-state game))
    	(assoc (core/move-piece game (:origin (move-components move)) (:destination (move-components move))) :mode mode)
      (recur 
      	(input-for-player player " That is not a valid move - what is your move (from/to)?")))))

(defmethod process-round :piece-placement [mode game piece player]
  (loop [move (input-for-piece player " Where do you want to place this piece?" game)]
    (if (valid-placement? move (:game-state game))
    	(assoc (core/update-game game piece move) :mode mode)
      (recur 
      	(input-for-piece piece " That is not a valid position - where do you want to place this piece?" game)))))

(defmethod process-round :piece-removal [mode game piece player]
  (loop [location-to-remove (input-for-piece player  (str " Mill completed! Which piece do you want to remove " (find-pieces game player) "?") game)]
    (if (valid-removal? location-to-remove (:game-state game))
    	(assoc (core/remove-piece game location-to-remove) :mode mode)
      (recur 
      	(input-for-piece piece " That is not a valid position - which piece to remove?" game)))))

(defmethod process-round :game-over [mode game piece player]
	(println "Game over!"))

(def ^:const existing-game-config-file "resources/save-state.clj")

(defn- reload-saved-game [saved-game]
	(println "Found saved game at " saved-game)
	(let [saved-game (read-string (slurp existing-game-config-file))]
		(pp/pprint saved-game)
		saved-game))

(defn- init-or-load-game []
	(if (.exists (io/as-file existing-game-config-file))
		(reload-saved-game existing-game-config-file)
		(core/init-game)))

(defn -main [& args]
	(println "Welcome to Nine Men's Morris!")
	(loop [	game (init-or-load-game) 
					piece (choose-piece game) 
					current-player (:current-player game)
					mode (:mode game)]
		(show game)
		(let [game-in-progress (assoc (process-round mode game piece current-player) :current-player current-player)
					next-player (choose-player game-in-progress)
					next-piece (choose-piece game-in-progress)]
			(cond 
				(:completed-mill-event game-in-progress)
		    	(recur game-in-progress piece next-player :piece-removal)
				(:game-over-event game-in-progress)
					(process-round :game-over)
				(not next-piece) ; next-piece has no remaining pieces
					(recur game-in-progress nil next-player :piece-movement)
				:else
	    		(recur game-in-progress next-piece next-player :piece-placement)))))
