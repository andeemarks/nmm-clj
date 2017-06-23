(ns morris.piece)

(defn- make-white-piece [id] (str :white "-" id))
(defn- make-black-piece [id] (str :black "-" id))

(defn make-white-pieces []
	(map #(make-white-piece %) (range 1 9)))

(defn make-black-pieces []
	(map #(make-black-piece %) (range 1 9)))
