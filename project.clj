(defproject morris "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [	[org.clojure/clojure "1.8.0"]
                  [io.aviso/pretty "0.1.34"]
                  [com.taoensso/timbre "4.10.0"]
                  [metosin/compojure-api "1.1.10"]
  								[aysylu/loom "1.0.0"]]
	:main morris.fe.game
  :ring {:handler morris.be.api/app}
  :profiles {:dev 
              {
                :plugins [[lein-ring "0.10.0"]]
                :dependencies [[midje "1.8.3"]]}
                :midje {}})

  
