(ns morris.input
  (:require [clojure.string :as str]
            [io.aviso.ansi :refer :all]))

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

(defn- piece-label [player game]
	(assert player "Attempting to build label for nil player!!!")
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str player "-bg")))
				white-piece-pool-size (count (:white-pieces game))
				black-piece-pool-size (count (:black-pieces game))]
		(piece-colour-code (str bold-red-font " [" player "] " white-piece-pool-size "/white " black-piece-pool-size "/black remaining " reset-font))))

(defn- player-label [player]
	(let [piece-colour-code (ns-resolve 'io.aviso.ansi (symbol (str player "-bg")))]
		(piece-colour-code (str bold-red-font " [" player "] " reset-font))))

(defn for-piece [player prompt game]
	(keyword (get-input (str (piece-label player game) prompt))))

(defn for-player [player prompt]
	(get-input (str (player-label player) prompt)))
