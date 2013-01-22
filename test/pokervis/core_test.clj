(ns pokervis.core-test
  (:use clojure.test
        pokervis.core))

(deftest samesuit-false
         (testing "Testing samesuit false"
                  (is
                      (= false
                         (samesuit (list {:suit :spades, :rank :ace},
                                          {:suit :hearts, :rank :ace}))))))
         
(deftest samesuit-true
         (testing "Testing samesuit true ace high"
                  (is
                      (= :ace
                         (samesuit (list {:suit :spades, :rank :ace},
                                          {:suit :spades, :rank :2},
                                          {:suit :spades, :rank :5}))))))

(deftest royalflush?-test
         (testing "Testing royalflush? for royal flush true")
         (is (= true
                (royalflush? (list {:suit :spades, :rank :ace},
                                   {:suit :spades, :rank :king},
                                   {:suit :spades, :rank :queen},
                                   {:suit :spades, :rank :jack},
                                   {:suit :spades, :rank :10})))))

(deftest royalflush?-false1
         (testing "Testing royalflush? for royal flush false; not same suit")
         (is (= false
                (royalflush? (list {:suit :spades, :rank :ace},
                                  {:suit :hearts, :rank :king},
                                  {:suit :spades, :rank :queen},
                                  {:suit :spades, :rank :jack},
                                  {:suit :spades, :rank :10})))))

(deftest royalflush?-false2
         (testing "Testing royalflush? for royal flush false; wrong ranks")
         (is (= false
                (royalflush? (list {:suit :spades, :rank :ace},
                                   {:suit :hearts, :rank :king},
                                   {:suit :spades, :rank :queen},
                                   {:suit :spades, :rank :jack},
                                   {:suit :spades, :rank :5})))))

(deftest aflush-true
         (testing "Testing aflush for flush")
         (is (= :ace
                (aflush (list {:suit :spades, :rank :ace}
                              {:suit :spades, :rank :2}
                              {:suit :spades, :rank :10}
                              {:suit :spades, :rank :king}
                              {:suit :spades, :rank :5})))))

(deftest aflush-true
         (testing "Testing aflush for not flush")
         (is (= false
                (aflush (list {:suit :spades, :rank :ace}
                              {:suit :spades, :rank :2}
                              {:suit :diamonds, :rank :10}
                              {:suit :spades, :rank :king}
                              {:suit :spades, :rank :5})))))

(deftest straight-testacelow
         (testing "Testing straight for ace low")
         (is (= :5
                (straight (list {:suit :spades, :rank :ace}
                                 {:suit :spades, :rank :2}
                                 {:suit :spades, :rank :4}
                                 {:suit :spades, :rank :3}
                                 {:suit :spades, :rank :5})))))

(deftest straight-false
         (testing "Testing straight for invalid straight")
         (is (= false
                (straight (list {:suit :spades, :rank :king}
                                {:suit :spades, :rank :2}
                                {:suit :spades, :rank :4}
                                {:suit :spades, :rank :3}
                                {:suit :spades, :rank :5})))))

(deftest straight-acehigh
         (testing "Testing straight for ace high")
         (is (= :ace
                (straight (list {:suit :spades, :rank :10}
                                {:suit :spades, :rank :ace}
                                {:suit :spades, :rank :king}
                                {:suit :spades, :rank :jack}
                                {:suit :spades, :rank :queen})))))

(deftest straightflush?-acelow
         (testing "Testing straightflush? for valid straight flush ace low")
         (is (= :5
                (straightflush? (list {:suit :spades, :rank :ace}
                                      {:suit :spades, :rank :2}
                                      {:suit :spades, :rank :4}
                                      {:suit :spades, :rank :3}
                                      {:suit :spades, :rank :5})))))

(deftest straightflush?-acehigh
         (testing "Testing straightflush? for valid straight flush ace high")
         (is (= :ace
                (straightflush? (list {:suit :spades, :rank :ace}
                                      {:suit :spades, :rank :king}
                                      {:suit :spades, :rank :queen}
                                      {:suit :spades, :rank :10}
                                      {:suit :spades, :rank :jack})))))

(deftest straightflush?-false
         (testing "Testing straightflush? for invalid straight flush")
         (is (= false
                (straightflush? (list {:suit :spades, :rank :ace}
                                      {:suit :hearts, :rank :2}
                                      {:suit :spades, :rank :4}
                                      {:suit :spades, :rank :3}
                                      {:suit :spades, :rank :5})))))

(deftest highcard-ace
         (testing "Testing highcard with ace")
         (is (= :ace
                (highcard (list {:rank :king, :suit :spades}
                                {:rank :king, :suit :hearts}
                                {:rank :3, :suit :diamonds}
                                {:rank :4, :suit :diamonds}
                                {:rank :ace, :suit :clubs})))))

(deftest kind-2
         (testing "Testing 2 of a kind")
         (is (= (list {:rank :king, :high :ace})
                (kind (list {:rank :king, :suit :spades}
                      {:rank :king, :suit :hearts}
                      {:rank :3, :suit :diamonds}
                      {:rank :4, :suit :diamonds}
                      {:rank :ace, :suit :clubs}) 2))))

(deftest kind-3
         (testing "Testing 3 of a kind")
         (is (= (list {:rank :king, :high :ace})
                (kind (list {:rank :king, :suit :spades}
                      {:rank :king, :suit :hearts}
                      {:rank :3, :suit :diamonds}
                      {:rank :king, :suit :diamonds}
                      {:rank :ace, :suit :clubs}) 3))))

(deftest kind-4
         (testing "Testing 4 of a kind")
         (is (= (list {:rank :king, :high :3})
                (kind (list {:rank :king, :suit :spades}
                      {:rank :king, :suit :hearts}
                      {:rank :3, :suit :diamonds}
                      {:rank :king, :suit :diamonds}
                      {:rank :king, :suit :clubs}) 4))))

(deftest two-pair
         (testing "Testing two pair")
         (is (= (list {:rank :king, :high :ace} {:rank :3, :high :ace})
                (kind (list {:rank :king, :suit :spades}
                      {:rank :king, :suit :hearts}
                      {:rank :3, :suit :diamonds}
                      {:rank :3, :suit :diamonds}
                      {:rank :ace, :suit :clubs}) 2))))

(deftest two-pair-diffhigh
         (testing "Testing two pair with different high cards for each pair")
         (is (= (list {:rank :king, :high :3} {:rank :3, :high :king})
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

; TODO: write a test to verify drawing x cards yields a deck of 52 - x count