(ns morris.be.api
	(:require [compojure.api.sweet :refer :all]
            [morris.common.board :as b]
						[morris.be.core :as core]
						[schema.core :as s]
            [loom.io :as g]
						[clojure.data.json :as json]
            [ring.logger.timbre :as log]
            [compojure.api.exception :as ex]
						[ring.util.http-response :refer :all]))

(defn bad-request-handler []
  (fn [^Exception e data request]
    (println "FFFFFFFFFFFFFFFFFFF")
    (bad-request {:message (.getMessage e), :type :bad-request})))

(def app
  (api
    ; {:exceptions 
    ;   {:handlers 
    ;     {
    ;       java.lang.IllegalArgumentException (bad-request-handler)
    ;     }}}
    {:swagger
     {:ui "/api-docs"
      :spec "/swagger.json"
      :data {:info {:title "Morris API"
                    :description "Server API for Nine Men's Morris game"}
             :tags [{:name "morris", :description "some apis"}]}}}

    (context "/game" []
      :middleware [log/wrap-with-logger]
      :tags ["api"]

      (GET "/state" []
        :return core/GameState
        :summary "Return a new game-state"
        (ok (core/init-game)))

      (POST "/piece/:piece/:destination" []
        :return core/GameState
      	:path-params [piece :- core/Piece destination :- core/Location]
        :body [game core/GameState]
        :summary "Adds a specified piece to the board"
        (ok (core/place-piece game piece destination)))

      (PUT "/piece/:origin/:destination" []
        :return core/GameState
      	:path-params [origin :- core/Location destination :- core/Location]
        :body [game core/GameState]
        :summary "Moves a piece from one location to another on the board"
        (ok (core/move-piece game origin destination)))

      (DELETE "/piece/:location" []
        :return core/GameState
      	:path-params [location :- core/Location]
        :body [game core/GameState]
        :summary "Removes a specified piece from the board"
        (ok (core/remove-piece game location)))
      )))
