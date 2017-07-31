(ns morris.fe.api
  (:require [cheshire.core :as cheshire]
  					[morris.be.core :as core]
  					[org.httpkit.client :as http]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]))

(log/merge-config! {:appenders {:spit (appenders/spit-appender {:fname "morris.log"})}})
(log/merge-config! {:appenders {:println nil}})

(defn- parse-body [body]
  (cheshire/parse-string body true))

(defn- build-options [body]
	{
		:body body
		:headers {"Content-Type" "application/json" "Accept" "application/json"}
		:timeout 2000})

(defn move-piece [game-state origin destination]
	(let [url (str "http://localhost:3000/game/piece/" (name origin) "/" (name destination))
				body (cheshire/generate-string game-state)
				options (build-options body)
				{:keys [status headers body error] :as resp} @(http/put url options)]
	  (if error
	  	(throw (Exception. error))
		  (parse-body (:body resp)))))

(defn place-piece [game-state piece destination]
	(let [url (str "http://localhost:3000/game/piece/" piece "/" (name destination))
				body (cheshire/generate-string game-state)
				options (build-options body)
				{:keys [status headers body error] :as resp} @(http/post url options)]
	  (if error
	  	(throw (Exception. error))
		  (parse-body (:body resp)))))

(defn remove-piece [game-state location]
	(let [url (str "http://localhost:3000/game/piece/" (name location))
				body (cheshire/generate-string game-state)
				options (build-options body)
				{:keys [status headers body error] :as resp} @(http/delete url options)]
	  (if error
	  	(throw (Exception. error))
		  (parse-body (:body resp)))))
