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

(def ranks-inv (into {} (for [[key val] ranks] [val key])))

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

; (suit ranks ...) -> ({:suit :rank} ...)
(defn gencards [args]
  "Turns something like (:hearts :3 :4) into ({:suit :hearts :rank :3} {:suit :hearts :rank :4})"
  (for [eachrank (rest args)]
    {:suit (first args) :rank eachrank}))

; ((suit ranks...) ...) -> ({:suit :ranks} ...)
(defn genhand [args]
  "Sends a vector of shorthand cards to gencards.
   Example: (genhand [[:spades :2 :3 :4] [:hearts :king :queen]])
   Returns: ({:suit :spades, :rank :2} {:suit :spades, :rank :3} {:suit :spades, :rank :4}
   {:suit :hearts, :rank :king} {:suit :hearts, :rank :queen})"
  (mapcat gencards args))

; Macro to make defining hands a little less tedious by saving one pair of brackets
; Usage: (hand [:spades :2 :3 :4] [:hearts :king :queen])
(defmacro hand [& args]
  `(genhand [~@args]))

; deck -> {:card card, :deck deck}
(defn drawrand [adeck]
  "Draws a random card from a deck, returns a map containing the drawn card
  and a deck minus that card"
  (let [drawncard (rand-nth adeck)]
    {:card drawncard, :deck (remove #{drawncard} adeck)}))

; suit rank hand -> boolean
(defn cardin? [asuit arank ahand]
  "Checks if a card is in a hand."
  (not= nil ; some returns nil if the item isn't found, but we want false
        (some #(= {:suit asuit :rank arank} %)
              ahand)))

; (cards ...) deck -> deck
(defn removefromdeck [thecards adeck]
  "Returns a deck with the specified cards having been removed."
  ; Remove wants a set, so we put the vector thecards into a set
  (remove (into #{} thecards) adeck))


; int int -> (int ...)
(defn uniqueints [howmany maxval]
  "Generate a vector of unique integers up to a certain value."
  (if (> howmany maxval) nil  ; make sure we have enough vals for desired # ints
   ; Generate a lazy series of unique integers up to the size of maxval
   ; Take the set of that to remove dups; if it's not large enough try again.
    (let [a (take howmany (repeatedly #(rand-int maxval)))]
                 (if (= (count (set a)) howmany) a
                   (uniqueints howmany maxval)))))
                 

; int deck -> (cards ...)
(defn drawfromdeck [numdrawn adeck]
  "Helper function for drawmulti"
  (for [eachcard (uniqueints numdrawn (dec (count adeck)))]
    (nth adeck eachcard)))

; int deck -> {:drawncards (card ...), :remainingdeck deck}
(defn drawmulti [numdrawn adeck]
  (let [drawn (drawfromdeck numdrawn adeck)]
  {:drawncards drawn
   :remainingdeck (removefromdeck drawn adeck)}))

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
  "Returns whether a royal flush can be built from the cards passed in.
  Currently doesn't check to see if input is > 5 cards."
  (let [suit (:suit (first thecards))]
   (and (aflush thecards)
        (cardin? suit :ace thecards)
        (cardin? suit :jack thecards)
        (cardin? suit :queen thecards)
        (cardin? suit :king thecards)
        (cardin? suit :10 thecards))))

; (cards ...) -> {:high rank, :cards (cards ...)} or false
(defn straight [thecards]
  "Returns the high card and cards if a straight, else false;
  doesn't check for >5 cards"
  ; the maps are to convert card ranks to int values:
  (let [sortedranks (sort (map ranks (map :rank thecards)))]
    (cond
      (= sortedranks (list 2 3 4 5 14))
        {:high :5, :cards thecards}
      (not= nil (some #(= sortedranks %) straightlist))
        {:high (highcard thecards), :cards thecards}
      :else false)))
                 
; (cards ...) -> {:high rank, :cards (cards ...)} or false
(defn straightflush [thecards]
  "Returns high card and cards if a straight flush, else false;
  doesn't check for >5 cards"
  (let [checkstraight (straight thecards)
        checkflush (aflush thecards)]
    (if (and checkstraight checkflush)
      {:high (:high checkstraight), :cards thecards}
      false)))

; (cards ...) -> int -> ({:rank rank, :high rank, :cards (cards ...)}...)
; or () if no matches
(defn kind [thecards howmany]
  "Checks if a hand can meet the criteria for n-of-a-kind.
  Can return multiple satisfying ranks."
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
  (let [distribution (group-by :rank thecards)
        distcounts (set (map count (vals distribution)))]
    ; The for only executes if the values of the distribution of cards can be
    ; divided into two groups of 3 and 2. If they can, then we have a full
    ; house; otherwise, we don't have one and so return ()
    (for [eachrank (keys distribution) :when (= #{3 2} distcounts)]
      {(if (= 3 (count (vals (eachrank distribution))))
          :high  ; If the key is present 3 times, it is the high rank:
          :full) ; Otherwise it's only present twice, so it is the full rank
          ,  eachrank}))) ; val of the map is the current rank in the for loop

; (cards ...) -> {:high rank, :full rank} or false
(defn fullhouse [thecards]
  "Returns the high rank and full rank if a full house, else false;
  doesn't check for > 5 cards"
  (let [genlist (genfullhouselist thecards)]
    ; genlist returns () if not a full house, but we want false
    (if (= () genlist)
      false
      ; The for in genfullhouselist gives us back the data as a list of two
      ; maps. This will pull the maps out of the list and merge them into one.
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

