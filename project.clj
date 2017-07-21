(defproject morris "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [	[org.clojure/clojure "1.8.0"]
                  [io.aviso/pretty "0.1.34"]
                  [com.taoensso/timbre "4.10.0"]
  								[aysylu/loom "1.0.0"]]
	:main morris.game
  :profiles {:dev {:dependencies [[midje "1.8.3"]]}
             ;; You can add dependencies that apply to `lein midje` below.
             ;; An example would be changing the logging destination for test runs.
             :midje {}})
             ;; Note that Midje itself is in the `dev` profile to support
             ;; running autotest in the repl.

  
