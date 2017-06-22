(ns morris.core
	(:require 
		[loom.graph :refer :all]
		[loom.io :refer :all]
		))

(def g (graph [:a1 :d1]
			[:d1 :g1]
			[:g1 :g4]
			[:g4 :g7]
			[:g7 :d7]
			[:d7 :a7]
			[:a7 :a4]
			[:a4 :a1]
			[:c3 :d3]
			[:c3 :c4]
			[:d3 :e3]
			[:e3 :e4]
			[:c4 :c5]
			[:e4 :e5]
			[:c5 :d5]
			[:d5 :e5]
			[:b2 :d2]
			[:d2 :f2]
			[:b2 :b4]
			[:b4 :b6]
			[:b6 :d6]
			[:d6 :f6]
			[:f6 :f4]
			[:f4 :f2]
			[:b4 :c4]
			[:f4 :e4]
			[:d2 :d3]
			[:d6 :d5]
			[:d1 :d2]
			[:a4 :b4]
			[:d7 :d6]
			[:g4 :f4]))

(def mills [
		(subgraph g [:a1 :d1 :g1])
		(subgraph g [:b2 :d2 :f1])
		(subgraph g [:c3 :d3 :e3])
		(subgraph g [:a4 :b4 :c4])
		(subgraph g [:e4 :f4 :g4])
		(subgraph g [:c5 :d5 :e5])
		(subgraph g [:b6 :d6 :f6])
		(subgraph g [:a7 :d7 :g7])
		(subgraph g [:a1 :a4 :a7])
		(subgraph g [:b2 :b4 :b6])
		(subgraph g [:c3 :c4 :c5])
		(subgraph g [:d1 :d2 :d3])
		(subgraph g [:d5 :d6 :d7])
		(subgraph g [:e3 :e4 :e5])
		(subgraph g [:f2 :f4 :f6])
		(subgraph g [:g1 :g4 :g7])
		])

(defn completed? [location-set]
	(= 3 ((frequencies location-set) true)))

(defn potential? [location-set]
	(= 1 ((frequencies location-set) false)))
