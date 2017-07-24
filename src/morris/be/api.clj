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


      ; (POST "/piece" []
      ;   :return Game
      ;   :body [pizza Pizza]
      ;   :summary "Adds a specified piece to the board"
      ;   (ok pizza))

      ; (PUT "/piece" []
      ;   :return Board
      ;   :body [pizza Pizza]
      ;   :summary "Moves a piece from one location to another on the board"
      ;   (ok pizza))

      ; (DELETE "/piece" []
      ;   :return Board
      ;   :body [pizza Pizza]
      ;   :summary "Removes a specified piece from the board"
      ;   (ok pizza))
      )))
