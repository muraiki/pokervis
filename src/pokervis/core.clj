(ns pokervis.core)

(def suits [:clubs :diamonds :hearts :spades])

(def cards [:2, :3, :4, :5, :6, :7, :8, :9, :10, :jack, :queen, :king, :ace])

(def deck (for [s suits, c cards] {:suit s, :rank c}))

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

; deck -> {:card card, :deck deck}
(defn drawrand [adeck]
  "Draws a random card from a deck, returns a list containing the drawn card and a deck minus that card"
  (let [drawncard (nth adeck (rand-int (count adeck)))]
    {:card drawncard, :deck (remove #{drawncard} adeck)}))

; int deck {nil} -> {:drawncards (card ...), :remainingdeck deck}
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

; (cards ...) -> boolean
(defn samesuit? [thecards]
  "Returns whether all cards passed in are the same suit or not."
  (let [suit (:suit (first thecards))]
    (every? #(= suit (:suit %)) thecards)))

; (cards ...) -> boolean
(defn royalflush? [thecards]
  "Returns whether a royal flush can be built from the cards passed in. Currently doesn't check to see if input is > 5 cards."
  (let [suit (:suit (first thecards))]
   (and (samesuit? thecards)
        ; TODO: Using java.util.Collection#contains()
        ; Won't work in clojurescript
        ; Try variant of (some #(= 1 %) (1 2 3))
        (.contains thecards {:suit suit, :rank :ace})
        (.contains thecards {:suit suit, :rank :king})
        (.contains thecards {:suit suit, :rank :queen})
        (.contains thecards {:suit suit, :rank :jack})
        (.contains thecards {:suit suit, :rank :10}))))

