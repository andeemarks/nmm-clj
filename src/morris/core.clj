(ns morris.core
	(:require [loom.graph :refer :all]))

(def g (graph [:A1 :D1]
[:D1 :G1]
[:G1 :G4]
[:G4 :G7]
[:G7 :D7]
[:D7 :A7]
[:A7 :A4]
[:A4 :A1]
[:C3 :D3]
[:C3 :C4]
[:D3 :E3]
[:E3 :E4]
[:C4 :C5]
[:E4 :E5]
[:C5 :D5]
[:D5 :E5]
[:B2 :D2]
[:D2 :F2]
[:B2 :B4]
[:B4 :B6]
[:B6 :D6]
[:D6 :F6]
[:F6 :F4]
[:F4 :F2]
[:B4 :C4]
[:F4 :E4]
[:D2 :D3]
[:D6 :D5]
[:D1 :D2]
[:A4 :B4]
[:D7 :D6]
[:G4 :F4]))

(defn completed? [location-set]
	(= 3 ((frequencies location-set) true)))

(defn potential? [location-set]
	(= 1 ((frequencies location-set) false)))
