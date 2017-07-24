(ns morris.be.piece
  (:require [clojure.string :as str]))

(defn- make-white-piece [id] (str "white-" id))
(defn- make-black-piece [id] (str "black-" id))

(defn make-white-pieces []
	(map make-white-piece (range 1 10)))

(defn make-black-pieces []
	(map make-black-piece (range 1 10)))

(defn extract-colour [id]
	(assert id "No id found to extract colour from")
	(first (str/split id #"-")))

(defn from-same-player? [pieces]
	(= 1 (count (set (map extract-colour (filter some? pieces))))))

(defn is-from-player? [piece player]
	(= player (extract-colour piece)))

