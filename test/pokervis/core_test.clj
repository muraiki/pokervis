(ns pokervis.core-test
  (:use clojure.test
        pokervis.core))

(deftest samesuit?-test
         (testing "Testing samesuit?"
                  (is
                    (and
                      (= false
                         (samesuit? (list {:suit :spades, :rank :ace},
                                          {:suit :hearts, :rank :ace})))
                      (= true
                         (samesuit? (list {:suit :spades, :rank :ace},
                                          {:suit :spades, :rank :2},
                                          {:suit :spades, :rank :5})))))))

(deftest royalflush?-test
         (testing "Testing royalflush? for royal flush true")
         (is (= true
                (royalflush? (list {:suit :spades, :rank :ace},
                                   {:suit :spades, :rank :king},
                                   {:suit :spades, :rank :queen},
                                   {:suit :spades, :rank :jack},
                                   {:suit :spades, :rank :10})))))

(deftest royalflush?-testfalse1
         (testing "Testing royalflush? for royal flush false; not same suit")
         (is (= false
                (royalflush? (list {:suit :spades, :rank :ace},
                                  {:suit :hearts, :rank :king},
                                  {:suit :spades, :rank :queen},
                                  {:suit :spades, :rank :jack},
                                  {:suit :spades, :rank :10})))))

(deftest royalflush?-testfalse2
         (testing "Testing royalflush? for royal flush false; wrong ranks")
         (is (= false
                (royalflush? (list {:suit :spades, :rank :ace},
                                  {:suit :hearts, :rank :king},
                                  {:suit :spades, :rank :queen},
                                  {:suit :spades, :rank :jack},
                                  {:suit :spades, :rank :5})))))

(deftest straight?-testacelow
         (testing "Testing straight? for ace low")
         (is (= true
                (straight? (list {:suit :spades, :rank :ace}
                                 {:suit :spades, :rank :2}
                                 {:suit :spades, :rank :4}
                                 {:suit :spades, :rank :3}
                                 {:suit :spades, :rank :5})))))

(deftest straight?-testfalse
         (testing "Testing straight? for invalid straight")
         (is (= false
                (straight? (list {:suit :spades, :rank :king}
                                 {:suit :spades, :rank :2}
                                 {:suit :spades, :rank :4}
                                 {:suit :spades, :rank :3}
                                 {:suit :spades, :rank :5})))))

(deftest straight?-testacehigh
         (testing "Testing straight? for ace high")
         (is (= true
                (straight? (list {:suit :spades, :rank :10}
                                 {:suit :spades, :rank :ace}
                                 {:suit :spades, :rank :king}
                                 {:suit :spades, :rank :jack}
                                 {:suit :spades, :rank :queen})))))

(deftest straightflush?-true
         (testing "Testing straightflush? for valid straight flush")
         (is (= true
                (straightflush? (list {:suit :spades, :rank :ace}
                                      {:suit :spades, :rank :2}
                                      {:suit :spades, :rank :4}
                                      {:suit :spades, :rank :3}
                                      {:suit :spades, :rank :5})))))

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

;(deftest two-pair
;         (testing "Testing two pair")
;         (is (= (list {:

; TODO: write a test to verify drawing x cards yields a deck of 52 - x count