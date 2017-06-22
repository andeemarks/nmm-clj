(ns morris.core)

(defn completed? [location-set]
	(= 3 ((frequencies location-set) true)))

(defn potential? [location-set]
	(= 1 ((frequencies location-set) false)))
