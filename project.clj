(defproject browser-fuzzer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-webdriver "0.7.2"]
                 [org.seleniumhq.selenium/selenium-java "2.53.1"]
                 [org.seleniumhq.selenium/selenium-remote-driver "2.53.1"]
                 [org.seleniumhq.selenium/selenium-server "2.53.1"]
                 [org.clojure/core.async "0.2.395"]]
  :main ^:skip-aot browser-fuzzer.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
