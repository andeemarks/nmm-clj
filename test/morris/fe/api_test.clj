(ns morris.fe.api-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [org.httpkit.fake :as faker]
            [morris.fe.api :refer :all]
            [morris.be.core :as core]
            ))

(log/merge-config! {:appenders nil})

(def ^:const new-game (core/init-game))

(facts "when the API calls are successful" :integration
  (fact "place piece succeeds"
    (:a1 (:pieces-on-board (place-piece new-game "white-1" "a1"))) => "white-1")
  (fact "move piece succeeds"
    (:a4 (:pieces-on-board (move-piece (assoc new-game :pieces-on-board {:a1 "white-1"}) "a1" "a4"))) => "white-1")
  (fact "remove piece succeeds"
    (let [pieces-on-board (:pieces-on-board (remove-piece (assoc new-game :pieces-on-board {:a1 "black-1"}) "a1"))]
      pieces-on-board =not=> nil
      (:a1 pieces-on-board) => nil))
  )

(faker/with-fake-http []    
  (facts "when the API calls fail"
    (fact "place piece throws an exception"
      (place-piece new-game "white-1" "a1") => (throws Exception))
    (fact "move piece throws an exception"
      (move-piece (assoc new-game :pieces-on-board {:a1 "white-1"}) "a1" "a4")  => (throws Exception))
    (fact "remove piece throws an exception"
      (remove-piece (assoc new-game :pieces-on-board {:a1 "black-1"}) "a1")  => (throws Exception))
    ))
