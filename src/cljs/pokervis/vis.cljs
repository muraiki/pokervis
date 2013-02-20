(ns pokervis.vis
  (:use-macros [c2.util :only [bind!]])
  (:use [c2.core :only [unify]])
  (:require [c2.scale :as scale]
            [pokervis.core :as pv]))

; Atom to keep track of all drawn cards
(def !results
;  (atom []))
  (atom [:threekind :highcard :onepair :threekind :threekind :onepair :onepair :fullhouse]))

; How many cards to draw at once; 7 for Texas Hold Em
(def howmanytodraw 7)

(defn begin-simulation []
  "Start generating hands and visualizing them"
  ; TODO: This will cause the browser to become unresponsive :)
  ; I need to limit the rate at which this occurs. Also, I might want to do it in batches at a time
  ; instead of updating the graphs with every single new draw.
  (while [true? true]
    (swap! !results conj (:best (pv/bestallhands (:drawncards (pv/drawmulti howmanytodraw pv/deck)))))))

; Copied from c2 hello bars demo for now
(bind! "#poker"
  (let [width 500 bar-height 20
        data (frequencies @!results)
        s (scale/linear :domain [0 (apply max (vals data))]
                        :range [0 width])]

    [:div
     (unify data (fn [[label val]]
                   [:div {:style {:height bar-height
                                  :width (s val)
                                  :background-color "gray"}}
                      [:span {:style {:color "white"}} label]]))]))