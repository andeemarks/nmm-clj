(ns morris.be.api
	(:require [compojure.api.sweet :refer :all]
						[morris.common.board :as b]
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

(s/defschema Game {
	:current-player	String
  :white-pieces  	[String]
  :black-pieces  	[String]
  :game-state 		String
  :mode 					String
  (s/optional-key :completed-mill-event) String
  (s/optional-key :game-over-event) String})

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
        :return Game
      	:path-params [piece :- String destination :- String]
        :body [game Game]
        :summary "Adds a specified piece to the board"
        (ok game))

			; (defn move-piece [game origin destination]
      (PUT "/piece/:origin/:destination" []
        :return Game
      	:path-params [origin :- String destination :- String]
        :body [game Game]
        :summary "Moves a piece from one location to another on the board"
        (ok game))

			; (defn remove-piece [game location-containing-piece]
      (DELETE "/piece/:location" []
        :return Game
      	:path-params [location :- String]
        :body [game Game]
        :summary "Removes a specified piece from the board"
        (ok game))
      )))
