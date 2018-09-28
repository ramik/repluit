(ns repluit.mouse
  (:require [repluit.impl.input :as input]
            [repluit.core :refer [-run-mutation -browser]]))

(defn move! [x y]
  {:pre [(number? x)
         (number? y)]}
  (-run-mutation
    (input/mouse-move! (-browser) {:x x :y y})))

(defn click! [x y]
  {:pre [(number? x)
         (number? y)]}
  (-run-mutation
    (input/mouse-click! (-browser) {:x x :y y})))
