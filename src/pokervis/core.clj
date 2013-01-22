(ns pokervis.core)

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
               10 :royalflush,
               9 :straightflush,
               8 :fourkind,
               7 :fullhouse,
               6 :flush,
               5 :straight,
               4 :threekind,
               3 :twopair,
               2 :onepair,
               1 :highcard
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

; (cards ...) -> suit or false
(defn samesuit [thecards]
  "Returns whether all cards passed in are the same suit or not."
  (let [suit (:suit (first thecards))]
    (if (every? #(= suit (:suit %)) thecards)
      (highcard thecards)
      false)))

; (cards ...) -> boolean
(defn royalflush? [thecards]
  "Returns whether a royal flush can be built from the cards passed in. Currently doesn't check to see if input is > 5 cards."
  (let [suit (:suit (first thecards))]
   (and (samesuit thecards)
        ; TODO: Using java.util.Collection#contains()
        ; Won't work in clojurescript
        ; Try variant of (some #(= 1 %) (1 2 3))
        (.contains thecards {:suit suit, :rank :ace})
        (.contains thecards {:suit suit, :rank :king})
        (.contains thecards {:suit suit, :rank :queen})
        (.contains thecards {:suit suit, :rank :jack})
        (.contains thecards {:suit suit, :rank :10}))))

; (cards ...) -> rank or false
; called aflush because flush is in clojure.core
(defn aflush [thecards]
  "Returns whether cards are a flush or not; doesn't check for >5 cards"
  (samesuit thecards))

; (cards ...) -> rank or false
(defn straight [thecards]
  "Returns whether cards are a straight or not; doesn't check for >5 cards"
  (let [sortedranks (sort (map ranks (map :rank thecards)))]
    (cond
      (= sortedranks (list 2 3 4 5 14))
        :5
      (.contains straightlist (sort (map ranks (map :rank thecards))))
        (highcard thecards)
      :else false)))
                 

; (cards ...) -> boolean
(defn straightflush? [thecards]
  "Returns whether cards are a straight flush or not; doesn't check for >5 cards"
  (and (aflush thecards) (straight thecards)))

; (cards ...) -> int -> ({:rank rank, :high rank}...) or () if no matches
(defn kind [thecards howmany]
  "Checks if a hand can meet the criteria for n-of-a-kind. Can return multiple satisfying ranks."
  (let [distribution (group-by :rank thecards)]
    (remove false?
            (for [k (keys distribution)]
              (if (= howmany (count (k distribution)))
                {:rank k
                 :high (highcard (remove #(= (:rank %) k) thecards))}
                false)))))    



