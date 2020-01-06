(ns starter.utils
  (:require [phaser :as Phaser]
            [starter.macros :as macros]))


(defn make-array-2d
  [width height callback]
  (let [ arr (make-array js/Array height width) ]
    (doseq [x (range width)
            y (range height)]
      (aset arr y x (callback x y)))
    arr))

(macros/defscene hello
  :atoms [controls (atom nil)
          map-layer (atom nil)]
  :on-preload (fn [this] (print this)))
