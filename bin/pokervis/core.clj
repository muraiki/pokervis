(ns pokervis.core
  (:require [clojure.math.combinatorics :as combo]))

(def suits [:clubs :diamonds :hearts :spades])

(def ranks {:2 2,
            :3 3,
            :4 4,
            :5 5,
            :6 6,
            :7 7,
            :8 8,
            :9 9,
            :10 10,
            :jack 11,
            :queen 12,
            :king 13,
            :ace 14})

(def ranks-inv {2 :2,
                3 :3,
                4 :4,
                5 :5,
                6 :6,
                7 :7,
                8 :8,
                9 :9,
                10 :10,
                11 :jack,
                12 :queen,
                13 :king,
                14 :ace})

(def deck (for [s suits, c (keys ranks)] {:suit s, :rank c}))

(def rankings {
               :royalflush 10,
               :straightflush 9,
               :fourkind 8,
               :fullhouse 7,
               :flush 6,
               :straight 5,
               :threekind 4,
               :twopair 3,
               :onepair 2,
               :highcard 1
               })

; List of lists of rank values that qualify as straights
(def straightlist (merge
                (partition 5 1 (sort (vals ranks)))
                (list 2 3 4 5 14))) ; Ace low

; deck -> {:card card, :deck deck}
(defn drawrand [adeck]
  "Draws a random card from a deck, returns a list containing the drawn card and a deck minus that card"
  (let [drawncard (nth adeck (rand-int (count adeck)))]
    {:card drawncard, :deck (remove #{drawncard} adeck)}))

; int deck {nil} -> {:drawncards (card ...), :remainingdeck deck}
; TODO: Probably would be faster to just draw n cards at once instead of using recursion
(defn drawmultihelper [remaining adeck accresults]
  "Used by drawmulti; do not call on its own. Currently not tail call optimized."
  (if (zero? remaining)
    accresults
    (let [result (drawrand adeck)]
      (drawmultihelper (dec remaining)
                       (:deck result)
                       {:drawncards (cons (:card result) (:drawncards accresults))
                             :remainingdeck (:deck result)}))))

; int deck -> {:drawncards (card ...), :remainingdeck deck}
(defn drawmulti [numdrawn adeck]
  "Draws a few cards from a deck. Returns drawn cards and remaining deck."
    (drawmultihelper numdrawn adeck {}))

; (cards ...) -> rank
(defn highcard [thecards]
  "Returns the rank of the highest valued card."
  (ranks-inv
    (last
      (sort (map ranks (map :rank thecards))))))

; (cards ...) -> {:high card, :cards (cards ...)} or false
; called aflush because flush is in clojure.core
(defn aflush [thecards]
  "Returns the high card and cards if a flush, else false"
  (let [suit (:suit (first thecards))]
    (if (every? #(= suit (:suit %)) thecards)
      {:high (highcard thecards), :cards thecards}
      false)))

; (cards ...) -> boolean
(defn royalflush? [thecards]
  "Returns whether a royal flush can be built from the cards passed in. Currently doesn't check to see if input is > 5 cards."
  (let [suit (:suit (first thecards))]
   (and (aflush thecards)
        ; TODO: Using java.util.Collection#contains()
        ; Won't work in clojurescript
        ; Try variant of (some #(= 1 %) (1 2 3))
        (.contains thecards {:suit suit, :rank :ace})
        (.contains thecards {:suit suit, :rank :king})
        (.contains thecards {:suit suit, :rank :queen})
        (.contains thecards {:suit suit, :rank :jack})
        (.contains thecards {:suit suit, :rank :10}))))

; (cards ...) -> {:high rank, :cards (cards ...)} or false
(defn straight [thecards]
  "Returns the high card and cards if a straight, else false; doesn't check for >5 cards"
  (let [sortedranks (sort (map ranks (map :rank thecards)))] ; maps are to convert card ranks to int values
    (cond
      (= sortedranks (list 2 3 4 5 14))
        {:high :5, :cards thecards}
      (.contains straightlist sortedranks)
        {:high (highcard thecards), :cards thecards}
      :else false)))
                 
; (cards ...) -> {:high rank, :cards (cards ...)} or false
(defn straightflush [thecards]
  "Returns high card and cards if a straight flush, else false; doesn't check for >5 cards"
  (let [checkstraight (straight thecards)
        checkflush (aflush thecards)]
    (if (and checkstraight checkflush)
      {:high (:high checkstraight), :cards thecards}
      false)))

; (cards ...) -> int -> ({:rank rank, :high rank, :cards (cards ...)}...) or () if no matches
(defn kind [thecards howmany]
  "Checks if a hand can meet the criteria for n-of-a-kind. Can return multiple satisfying ranks."
  (let [distribution (group-by :rank thecards)]
    (remove false?
            (for [k (keys distribution)]
              (if (= howmany (count (k distribution)))
                {:rank k
                 :high (highcard (remove #(= (:rank %) k) thecards))
                 :cards thecards}
                false)))))    

; (cards ...) -> ({:high rank} {:full rank}) or ()
; This code is a bit ugly :(
(defn genfullhouselist [thecards]
  "Helper function for fullhouse"
  (let [distribution (group-by :rank thecards)]
    ; The for only executes if the values of the distribution of cards can be divided into two groups.
    ; If they can, then we have a full house; otherwise, we don't have one and so return ()
    (for [k (keys distribution) :when (= 2 (count (vals distribution)))]
      {(if (= 3 (count (vals (k distribution)))) ; If the key is present 3 times, it is the high rank
          :high
          :full) ; Otherwise it's only present twice, so it is the full rank
            k} ; set the value of the generated map to the key
      )))

; (cards ...) -> {:high rank, :full rank} or false
(defn fullhouse [thecards]
  "Returns the high rank and full rank if a full house, else false; doesn't check for > 5 cards"
  (let [genlist (genfullhouselist thecards)]
    (if (= () genlist)
      false
      ; The for in genfullhouselist gives us back the data as a list of two maps.
      ; This will pull the maps out of the list and merge them into one.
      (merge (first genlist) (second genlist)))))

; (cards ...) -> {:best rankings-key, :result (cards ...)}
(defn besthand [thecards]
  "Returns the best possible hand for a given set of cards."
  (cond
    (royalflush? thecards) {:best :royalflush, :result thecards}
    (straightflush thecards) {:best :straightflush, :result thecards}
    (not= () (kind thecards 4)) {:best :fourkind, :result thecards}
    (fullhouse thecards) {:best :fullhouse, :result thecards}
    (aflush thecards) {:best :flush, :result thecards}
    (straight thecards) {:best :straight, :result thecards}
    (not= () (kind thecards 3)) {:best :threekind, :result thecards}
    (= 2 (count (kind thecards 2))) {:best :twopair, :result thecards}
    (not= () (kind thecards 2)) {:best :onepair, :result thecards}
    :else {:best highcard, :result thecards}))

; (cards ...) -> ({:best rankings-key, :result (cards ...)} ...)
(defn besthandcombo [thecards]
  "Returns the best possible hands for all combinations of given cards."
    (let [allhands (combo/combinations thecards 5)]
      (for [eachhand allhands]
        (besthand eachhand))))

; (cards ...) -> {:best rankings-key, :result (cards ...)}
(defn bestallhands [thecards]
  (first (sort-by #(:rankings (:best %)) (besthandcombo thecards))))
