(ns pokervis.core-test
  (:use clojure.test
        pokervis.core))

; TODO: write a test to verify drawing x cards yields a deck of 52 - x count

(deftest aflush-true
         (testing "Testing aflush for flush ace high")
         (is (= {:high :ace, :cards (hand [:spades :ace :2 :10 :king :5])}
                (aflush (hand [:spades :ace :2 :10 :king :5])))))

(deftest aflush-false
         (testing "Testing aflush for not flush")
         (is (= false
                (aflush (hand [:spades :ace :king :5 :4] [:diamonds :10])))))

(deftest royalflush?-test
         (testing "Testing royalflush? for royal flush true")
         (is (= true
                (royalflush? (hand [:spades :ace :king :jack :queen :10])))))

(deftest royalflush?-false1
         (testing "Testing royalflush? for royal flush false; not same suit")
         (is (= false
                (royalflush? (hand
                               [:spades :ace :king :queen :jack]
                               [:diamonds :10])))))

(deftest royalflush?-false2
         (testing "Testing royalflush? for royal flush false; wrong ranks")
         (is (= false
                (royalflush? (hand [:spades :ace :king :queen :jack :5])))))

(deftest straight-acelow
         (testing "Testing straight for ace low")
         (is (= {:high :5, :cards (hand [:spades :ace :2 :3 :4 :5])}
                (straight (hand [:spades :ace :2 :3 :4 :5])))))

(deftest straight-false
         (testing "Testing straight for invalid straight")
         (is (= false
                (straight (hand [:spades :king :2 :3 :4 :5])))))

(deftest straight-acehigh
         (testing "Testing straight for ace high")
         (is (= {:high :ace, :cards (hand [:spades :10 :ace :king :jack]
                                          [:hearts :queen])}
                 (straight (hand [:spades :10 :ace :king :jack]
                                 [:hearts :queen])))))

(deftest straightflush-acelow
         (testing "Testing straightflush for valid straight flush ace low")
         (is (= {:high :5, :cards (hand [:spades :ace :2 :3 :4 :5])}
                 (straightflush (hand [:spades :ace :2 :3 :4 :5])))))

(deftest straightflush-acehigh
         (testing "Testing straightflush for valid straight flush ace high")
         (is (= {:high :ace, :cards (hand
                                      [:spades :ace :king :queen :10 :jack])}
                (straightflush (hand [:spades :ace :king :queen :10 :jack])))))

(deftest straightflush-false
         (testing "Testing straightflush for invalid straight flush")
         (is (= false
                (straightflush (hand [:spades :ace :3 :4 :5] [:hearts :2])))))

(deftest highcard-ace
         (testing "Testing highcard with ace")
         (is (= :ace
                (highcard (hand [:spades :2 :5]
                                [:hearts :ace]
                                [:diamonds :10 :jack])))))

(deftest kind-2
         (testing "Testing 2 of a kind")
         (is (= (list {:rank :king, :high :ace, :cards
                       (hand [:hearts :king]
                             [:diamonds :king]
                             [:clubs :ace :3 :4])})
                (kind (hand [:hearts :king]
                            [:diamonds :king]
                            [:clubs :ace :3 :4]) 2))))

(deftest kind-3
         (testing "Testing 3 of a kind")
         (is (= (list {:rank :king, :high :ace,
                       :cards (list {:rank :king, :suit :spades}
                                    {:rank :king, :suit :hearts}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :king, :suit :diamonds}
                                    {:rank :ace, :suit :clubs})})
                (kind (list {:rank :king, :suit :spades}
                            {:rank :king, :suit :hearts}
                            {:rank :3, :suit :diamonds}
                            {:rank :king, :suit :diamonds}
                            {:rank :ace, :suit :clubs}) 3))))

(deftest kind-4
         (testing "Testing 4 of a kind")
         (is (= (list {:rank :king, :high :3,
                       :cards (list {:rank :king, :suit :spades}
                                    {:rank :king, :suit :hearts}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :king, :suit :diamonds}
                                    {:rank :king, :suit :clubs})})
                (kind (list {:rank :king, :suit :spades}
                            {:rank :king, :suit :hearts}
                            {:rank :3, :suit :diamonds}
                            {:rank :king, :suit :diamonds}
                            {:rank :king, :suit :clubs}) 4))))

(deftest two-pair
         (testing "Testing two pair")
         (is (= (list {:rank :king, :high :ace,
                       :cards (list {:rank :king, :suit :spades}
                                    {:rank :king, :suit :hearts}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :ace, :suit :clubs})}
                      {:rank :3, :high :ace,
                       :cards (list {:rank :king, :suit :spades}
                                    {:rank :king, :suit :hearts}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :ace, :suit :clubs})})
                (kind (list {:rank :king, :suit :spades}
                            {:rank :king, :suit :hearts}
                            {:rank :3, :suit :diamonds}
                            {:rank :3, :suit :diamonds}
                            {:rank :ace, :suit :clubs}) 2))))

(deftest two-pair-diffhigh
         (testing "Testing two pair with different high cards for each pair")
         (is (= (list {:rank :king, :high :3,
                       :cards (list {:rank :king, :suit :spades}
                                    {:rank :king, :suit :hearts}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :1, :suit :clubs})}
                      {:rank :3, :high :king,
                       :cards (list {:rank :king, :suit :spades}
                                    {:rank :king, :suit :hearts}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :3, :suit :diamonds}
                                    {:rank :1, :suit :clubs})})
                (kind (list {:rank :king, :suit :spades}
                            {:rank :king, :suit :hearts}
                            {:rank :3, :suit :diamonds}
                            {:rank :3, :suit :diamonds}
                            {:rank :1, :suit :clubs}) 2))))

(deftest two-pair-fail
         (testing "Testing two pair with no matching pair")
         (is (= ()
                (kind (list {:rank :king, :suit :spades}
                            {:rank :10, :suit :hearts}
                            {:rank :3, :suit :diamonds}
                            {:rank :4, :suit :diamonds}
                            {:rank :1, :suit :clubs}) 2))))

(deftest fullhouse-true
         (testing "Testing fullhouse ace high full of 5s"
                  (is (= {:high :ace, :full :5}
                         (fullhouse (list {:rank :ace, :suit :spades}
                                          {:rank :ace, :suit :hearts}
                                          {:rank :ace, :suit :diamonds}
                                          {:rank :5, :suit :clubs}
                                          {:rank :5, :suit :spades}))))))

(deftest fullhouse-false
         (testing "Testing fullhouse false"
                  (is (= false
                         (fullhouse (list {:rank :ace, :suit :spades}
                                          {:rank :ace, :suit :hearts}
                                          {:rank :ace, :suit :diamonds}
                                          {:rank :3, :suit :clubs}
                                          {:rank :5, :suit :spades}))))))

(deftest fullhouse-four-of-a-kind
         (testing "Four-of-a-kind should not be mistaken for full house"
                  (is (= false
                         (fullhouse (hand [:spades :ace :5]
                                          [:hearts :ace]
                                          [:diamonds :ace]
                                          [:clubs :ace]))))))

; TODO: Currently fails, returns one pair of 5s
; This will work if [:hearts :queen :king :ace :jack :10] is the first arg
; It seems like the problem actually lies in besthandcombo, where the val of
; :best is ending up in some instances as a function like
; #<core$highcard pokervis.core$highcard@59dc73f9> instead of a result 
(deftest bestallhands-fullhouse
  (testing "Testing bestallhands, royal flush"
           (is (= {:best :royalflush, :result (hand [:hearts :queen :10 :jack :ace :king]
                                                    [:clubs :5]
                                                    [:diamonds :5])}
                  (bestallhands (hand [:clubs :5]
                                      [:diamonds :5]
                                      [:hearts :queen :king :ace :jack :10]))))))

(deftest bestallhands-fourkind
  (testing "Testing bestallhands, four of a kind"
           (is (= {:best :fourkind, :result (list {:rank :5, :suit :hearts}
                                                  {:rank :5, :suit :diamonds}
                                                  {:rank :5, :suit :clubs}
                                                  {:rank :5, :suit :spades}
                                                  {:rank :3, :suit :hearts})}
                  (bestallhands (list {:rank :5, :suit :hearts}
                                      {:rank :5, :suit :diamonds}
                                      {:rank :5, :suit :clubs}
                                      {:rank :5, :suit :spades}
                                      {:rank :3, :suit :hearts}
                                      {:rank :2, :suit :spades}
                                      {:rank :10, :suit :diamonds}))))))


                            
