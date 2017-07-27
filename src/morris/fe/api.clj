(ns morris.fe.api
  (:require [cheshire.core :as cheshire]
  					[morris.be.core :as core]
  					[org.httpkit.client :as http]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]))

(log/merge-config! {:appenders {:spit (appenders/spit-appender {:fname "morris.log"})}})
(log/merge-config! {:appenders {:println nil}})

(defn move-piece [game-state origin destination]
	; (let [url (str "http://localhost:3000/game/piece/" origin "/" destination)
	; 			body (cheshire/generate-string game-state)
	; 			_ (println url)
	; 			_ (println body)
	; 			]
	; 	(println (client/put url {:body body}))))
)

(defn place-piece [game-state piece destination]
	(let [url (str "http://localhost:3000/game/piece/" piece "/" destination)
				body (cheshire/generate-string game-state)
				options {
					:url url
					:method :post
					:body body
					:headers {"Content-Type" "application/json" "Accept" "application/json"}
					:timeout 2000}
				{:keys [status headers body error] :as resp} @(http/post url options)]
	  (if error
	    (log/error "Failed, exception: " error)
	    (log/info "API place-piece success: " status))
	  resp))

(defn remove-piece [game-state location])