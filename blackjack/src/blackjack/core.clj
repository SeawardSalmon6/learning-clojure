(ns blackjack.core
  (:require [blackjack.game :refer [start-game]])
  (:gen-class))


(defn -main
  "Run blackjack"
  []
  (println "What is your name?")
  (start-game (read-line)))
