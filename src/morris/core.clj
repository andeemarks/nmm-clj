(ns morris.core
	(:require 
		[loom.graph :refer :all]
		[morris.board :as board]
		[morris.mill :as mill]
		[morris.piece :as piece]
		[loom.io :refer :all]
		))

(defn init-game []
	{:board (board/board)
		:white-pieces (piece/make-white-pieces)
		:black-pieces (piece/make-black-pieces)
		:game-state nil})

(defn update-game [game piece destination]
	(let [current-game-state (:game-state game)]

		(if (board/location-exists? destination)				
			(if (board/location-available? destination current-game-state)
				(let [new-game-state (merge current-game-state {destination piece})
							new-game (assoc game :game-state new-game-state)
							mill-completed? (mill/check-for-completed-mills new-game-state destination)]
					(if mill-completed?
						(assoc new-game :completed-mill-event "mill completed")
						(assoc new-game :completed-mill-event nil)))
				(throw (IllegalStateException. (str "Location " destination " is already occupied"))))
			(throw (IllegalArgumentException. (str "Location " destination " does not exist on board"))))))
