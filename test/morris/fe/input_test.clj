(ns morris.fe.input-test
  (:require [midje.sweet :refer :all]
            [taoensso.timbre :as log]
            [morris.fe.input :refer :all]
            ))

(log/merge-config! {:appenders nil})

(fact "decomposing a move separates the origin and destination"
  (move-components "a1/a4") => {:origin :a1 :destination :a4}
  (move-components "a1") => {:origin nil :destination nil}
  (move-components "/a1") => {:origin nil :destination nil}
  (move-components " a1/a4 ") => {:origin :a1 :destination :a4}
  (move-components " a1 / a4 ") => {:origin :a1 :destination :a4}
  (move-components "A1/B4") => {:origin :a1 :destination :b4}
  )
