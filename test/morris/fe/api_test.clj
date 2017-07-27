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
    (let [api-response (place-piece new-game "white-1" "a1")
      _ (println api-response)]
      (:status api-response) => 200)
  ))
