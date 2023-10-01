(ns blackjack.game
  (:require [card-ascii-art.core :as card]))


(defn new-card
  "Generates a card number between 1 and 13"
  []
  (inc (rand-int 13)))


(defn normalize-JQK-card
  "Returns a card number to 10 if it is a J, Q or K"
  [card]
  (if (> card 10) 10 card))


(defn normalize-A-card
  "Returns a card number to 11 if it is an A"
  [card]
  (if (= card 1) 11 card))


(defn get-cards-points
  "Returns the sum of the cards respecting the following rules:
   - J, Q and K are worth 10 points
   - A is worth 11 points if the sum of cards is less than 21, otherwise it is worth 1 point"
  [cards]
  (let [cards-without-JQK (map normalize-JQK-card cards)
        cards-without-A (map normalize-A-card cards-without-JQK)
        sum-with-A-as-11 (reduce + cards-without-A)
        sum-with-A-as-1 (reduce + cards-without-JQK)]
    (if (> sum-with-A-as-11 21) sum-with-A-as-1 sum-with-A-as-11)))


(defn player
  "Creates a new player with two cards and the sum of the points"
  [name]
  (let [card1 (new-card)
        card2 (new-card)
        cards [card1 card2]
        points (get-cards-points cards)]
    {:player-name name
     :cards       cards
     :points      points}))


(defn more-player
  "Adds a new card to the player and returns the player with more cards"
  [old-player]
  (let [card (new-card)
        cards (conj (:cards old-player) card)
        points (get-cards-points cards)]
    (assoc old-player :cards cards :points points)))


(defn player-wants-to-continue?
  "Asks the player if he wants another card"
  [player]
  (println)
  (println (str (:player-name player) ", do you want another card? (yes/no)"))
  (flush)
  (= (read-line) "yes"))


(defn dealer-wants-to-continue?
  "Returns true if the dealer has less than player points"
  [player-points dealer]
  (let [dealer-points (:points dealer)]
    (and (< player-points 21)
         (<= dealer-points player-points))))


(defn game
  "Asks the player if he wants another card and returns the player with more cards"
  [player fn-continue-decision?]
  (if (fn-continue-decision? player)
    (let [player-with-more-cards (more-player player)]
      (card/print-player player-with-more-cards)
      (recur player-with-more-cards fn-continue-decision?))
    player))


(defn get-winner-message
  "Returns the message of the winner"
  [player dealer]
  (let [player-points (:points player)
        dealer-points (:points dealer)
        player-name (:player-name player)
        dealer-name (:player-name dealer)
        dealer-wins-message (str dealer-name ", wins!")
        player-wins-message (str player-name ", wins!")]
    (cond
      (and (> player-points 21)
           (> dealer-points 21)) "Both lose!"
      (= player-points dealer-points) "Draw!"
      (> player-points 21) dealer-wins-message
      (> dealer-points 21) player-wins-message
      (> dealer-points player-points) dealer-wins-message
      (> player-points dealer-points) player-wins-message)))


(defn end-game
  "Prints the winner message and the cards of the player and the dealer"
  [player dealer]
  (let [message (get-winner-message player dealer)]
    (card/print-player dealer)
    (card/print-player player)
    (println message)))


(defn start-game
  "Starts the game"
  [player-name]
  (let [dealer (player "Dealer")
        my-player (player player-name)]

    (card/print-masked-player dealer)
    (card/print-player my-player)

    (let [my-player-after-game (game my-player player-wants-to-continue?)
          partial-dealer-wants-to-continue? (partial dealer-wants-to-continue? (:points my-player-after-game))
          dealer-after-game (game dealer partial-dealer-wants-to-continue?)]

      (end-game my-player-after-game dealer-after-game))))
