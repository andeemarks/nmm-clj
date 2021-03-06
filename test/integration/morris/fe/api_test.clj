(ns morris.fe.api-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.fe.api :refer :all]
            [morris.be.core :as core]
            ))

(log/merge-config! {:appenders nil})

(def ^:const new-game (core/init-game))

(facts :integration "when the API calls are successful" 
  (fact "init-game succeeds"
    (let [game-state (init-game)]
      (:current-player (init-game)) =not=> nil
      (:white-pieces (init-game)) =not=> nil
      (:black-pieces (init-game)) =not=> nil
      (:pieces-on-board (init-game)) =not=> nil
      (:mode (init-game)) =not=> nil)
    )
  (fact "place piece succeeds"
    (:a1 (:pieces-on-board (place-piece new-game "white-1" "a1"))) => "white-1")
  (fact "move piece succeeds"
    (:a4 (:pieces-on-board (move-piece (assoc new-game :pieces-on-board {:a1 "white-1"}) "a1" "a4"))) => "white-1")
  (fact "remove piece succeeds"
    (let [pieces-on-board (:pieces-on-board (remove-piece (assoc new-game :pieces-on-board {:a1 "black-1"}) "a1"))]
      pieces-on-board =not=> nil
      (:a1 pieces-on-board) => nil))
  )