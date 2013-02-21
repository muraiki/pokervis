(ns pokervis.main
  (:use [c2.event :only [on-load]])
  (:require [pokervis.vis :as vis]))

(defn main []
  "Init function to run on page load."
  (vis/animation-loop))

(on-load main)