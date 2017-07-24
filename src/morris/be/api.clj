(ns morris.be.api
	(:require [compojure.api.sweet :refer :all]
            [morris.common.board :as b]
						[morris.be.core :as core]
						[schema.core :as s]
            [loom.io :as g]
						[clojure.data.json :as json]
						[ring.util.http-response :refer :all]))

; (defapi app
;   (GET "/hello" []
;     :query-params [name :- String]
;     (ok {:message (str "Hello, " name)})))

; (s/defschema Board
;   s/Str)

(def app
  (api
    {:swagger
     {:ui "/api-docs"
      :spec "/swagger.json"
      :data {:info {:title "Morris API"
                    :description "Server API for Nine Men's Morris game"}
             :tags [{:name "morris", :description "some apis"}]}}}

    (context "/game" []
      :tags ["api"]

      (GET "/board" []
        :return s/Str
        :summary "Return an empty Board"
        (ok (g/dot-str (b/board))))

			; (defn place-piece [game piece destination]
      (POST "/piece/:piece/:destination" []
        :return core/GameState
      	:path-params [piece :- core/Piece destination :- core/Location]
        :body [game core/GameState]
        :summary "Adds a specified piece to the board"
        (ok game))

			; (defn move-piece [game origin destination]
      (PUT "/piece/:origin/:destination" []
        :return core/GameState
      	:path-params [origin :- core/Piece destination :- core/Location]
        :body [game core/GameState]
        :summary "Moves a piece from one location to another on the board"
        (ok game))

			; (defn remove-piece [game location-containing-piece]
      (DELETE "/piece/:location" []
        :return core/GameState
      	:path-params [location :- core/Location]
        :body [game core/GameState]
        :summary "Removes a specified piece from the board"
        (ok game))
      )))
