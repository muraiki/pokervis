(ns pokervis.vis
  (:use-macros [c2.util :only [bind!]])
  (:use [c2.core :only [unify]])
  (:require [c2.scale :as scale]
            [pokervis.core :as pv]
            [goog.dom :as dom]))

; Atom to keep track of all drawn cards
(def !results
  (atom {}))

; How many cards to draw: 7 for Texas Hold Em
(def howmanytodraw 7)

(defn genwinner []
  "Generates a new winning hand."
  (:best (pv/bestallhands
    (:drawncards (pv/drawmulti howmanytodraw pv/deck)))))

(defn simulation []
  "Start generating hands and visualizing them"
  ; Might be better to do this in batches
  ;(.log js/console (pr-str
  (let [winner (genwinner)
        currentval (winner @!results)]
        (swap! !results assoc winner (inc currentval))))

(defn animation-loop []
  (.requestAnimationFrame (dom/getWindow) animation-loop)
  (simulation))

(bind! "#poker"
  (let [width 600 bar-height 30
        data @!results
        s (scale/linear :domain [0 (apply max (vals data))]
                        :range [0 width])]

    [:div
     (unify data (fn [[label val]]
                   [:div {:style {:height bar-height
                                  :width (s val)
                                  :background-color "gray"}}
                      [:span {:style {:color "black"}} label]]))]))

(bind! "#freqs"
  (let [data @!results]
    [:div
      (pr-str data)]))

(bind! "#total"
  (let [data (reduce + (vals @!results))]
    [:div
      (pr-str data)]))