(ns morris.fe.api-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.fe.api :refer :all]
            [morris.be.core :as core]
            ))

(log/merge-config! {:appenders nil})

(def ^:const new-game (core/init-game))

(facts "calling the api"
  (fact "place piece succeeds"
    (:status (place-piece new-game "white-1" "a1")) => 200)
  (fact "move piece succeeds"
    (:status (move-piece (assoc new-game :pieces-on-board {:a1 "white-1"}) "a1" "a4")) => 200)
  (fact "remove piece succeeds"
    (:status (remove-piece (assoc new-game :pieces-on-board {:a1 "black-1"}) "a1")) => 200)
  )
