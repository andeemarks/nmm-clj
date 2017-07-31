(defproject morris "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :plugins [[io.aviso/pretty "0.1.34"]]
  :dependencies [	[org.clojure/clojure "1.8.0"]
                  [io.aviso/pretty "0.1.34"]
                  [com.taoensso/timbre "4.10.0"]
                  [ring-logger-timbre "0.7.5"]
                  [metosin/compojure-api "1.1.10"]
                  [http-kit "2.2.0"]
   								[aysylu/loom "1.0.0"]]
	:main morris.fe.game
  :ring {:handler morris.be.api/app}
  :aliases {"itest" ["midje" ":filters" "integration"]
            "utest" ["midje" ":filters" "-integration"]
            "ci"    ["test"]}
  :test-paths ["test/unit" "test/integration"]
  :profiles {:dev 
              {
                :plugins [[lein-ring "0.10.0"]]
                :dependencies [[ring/ring-mock "0.3.1"] [midje "1.8.3"] [http-kit.fake "0.2.1"]]}
                :midje {}})

  
