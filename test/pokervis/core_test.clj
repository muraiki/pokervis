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

; TODO: This test fails in lein test, but works in repl...
(deftest royalflush?-testfalse1
         (testing "Testing royalflush? for royal flush false; not same suit")
         (is (= false
                royalflush? (list {:suit :spades, :rank :ace},
                                  {:suit :hearts, :rank :king},
                                  {:suit :spades, :rank :queen},
                                  {:suit :spades, :rank :jack},
                                  {:suit :spades, :rank :10}))))

; TODO: This test fails in lein test, but works in repl...
(deftest royalflush?-testfalse2
         (testing "Testing royalflush? for royal flush false; wrong ranks")
         (is (= false
                royalflush? (list {:suit :spades, :rank :ace},
                                  {:suit :hearts, :rank :king},
                                  {:suit :spades, :rank :queen},
                                  {:suit :spades, :rank :jack},
                                  {:suit :spades, :rank :5}))))

; TODO: write a test to verify drawing x cards yields a deck of 52 - x count