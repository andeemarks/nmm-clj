(ns morris.core
	(:require 
		[loom.graph :refer :all]
		[loom.io :refer :all]
		))

(def board (graph [:a1 :d1]
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
		(subgraph board [:a1 :d1 :g1])
		(subgraph board [:b2 :d2 :f1])
		(subgraph board [:c3 :d3 :e3])
		(subgraph board [:a4 :b4 :c4])
		(subgraph board [:e4 :f4 :g4])
		(subgraph board [:c5 :d5 :e5])
		(subgraph board [:b6 :d6 :f6])
		(subgraph board [:a7 :d7 :g7])
		(subgraph board [:a1 :a4 :a7])
		(subgraph board [:b2 :b4 :b6])
		(subgraph board [:c3 :c4 :c5])
		(subgraph board [:d1 :d2 :d3])
		(subgraph board [:d5 :d6 :d7])
		(subgraph board [:e3 :e4 :e5])
		(subgraph board [:f2 :f4 :f6])
		(subgraph board [:g1 :g4 :g7])
		])

(defn completed-mill? [mills]
	(= 3 ((frequencies mills) true)))

(defn potential-mill? [mills]
	(= 1 ((frequencies mills) false)))
