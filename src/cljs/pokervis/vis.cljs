(ns pokervis.vis
  (:use-macros [c2.util :only [bind!]])
  (:use [c2.core :only [unify]])
  (:require [c2.scale :as scale]
            [pokervis.core :as pv]
            [goog.dom :as dom]))

; Atom to keep track of all drawn cards
(def !results
  (atom []))

; How many cards to draw: 7 for Texas Hold Em
(def howmanytodraw 7)

(defn simulation []
  "Start generating hands and visualizing them"
  ; Might be better to do this in batches
  ;(.log js/console (pr-str
    (swap! !results conj (:best (pv/bestallhands (:drawncards (pv/drawmulti howmanytodraw pv/deck))))))

(defn animation-loop []
  (.requestAnimationFrame (dom/getWindow) animation-loop)
  (simulation))

(bind! "#poker"
  (let [width 1000 bar-height 30
        data (frequencies @!results)
        s (scale/linear :domain [0 (apply max (vals data))]
                        :range [0 width])]

    [:div
     (unify data (fn [[label val]]
                   [:div {:style {:height bar-height
                                  :width (s val)
                                  :background-color "gray"}}
                      [:span {:style {:color "black"}} label]]))]))

(bind! "#freqs"
  (let [data (frequencies @!results)]
    [:div
      (pr-str data)]))