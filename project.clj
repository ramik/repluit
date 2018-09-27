(defproject repluit "0.0.1"
  :description "REPL powered UI Testing"
  :url "https://github.com/milankinen/repluit"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[clj-chrome-devtools "20180528"]
                 [org.clojure/tools.logging "0.4.1"]
                 [commons-io/commons-io "2.6"]
                 [hickory "0.7.1" :exclusions [org.clojure/clojure
                                               org.clojure/clojurescript
                                               viebel/codox-klipse-theme]]]
  :plugins [[lein-ancient "0.6.15"]]
  :profiles {:dev   {:dependencies [[org.clojure/clojure "1.9.0"]
                                    [ch.qos.logback/logback-classic "1.2.3"]]}
             :java9 {:jvm-opts ["--add-modules" "java.xml.bind"]}}
  :deploy-repositories [["releases" :clojars]]
  :aliases {"t" "test"}
  :release-tasks [["deploy"]])
