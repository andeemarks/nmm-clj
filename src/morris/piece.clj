(ns morris.piece
	(:require [clojure.string :as str]))

(defn- make-white-piece [id] (symbol (str "white-" id)))
(defn- make-black-piece [id] (symbol (str "black-" id)))

(defn make-white-pieces []
	(map #(make-white-piece %) (range 1 9)))

(defn make-black-pieces []
	(map #(make-black-piece %) (range 1 9)))

(defn extract-colour [id]
	(first (str/split (name id) #"-")))

(defn from-same-player? [pieces]
	(= 1 (count (set (map #(extract-colour %) (filter some? pieces))))))
