(ns starter.macros
  (:require [phaser :as Phaser])
  (:require-macros [starter.macros]))

(defn defscene*
  [options scene]
  (let [{:keys [on-preload on-create on-update]
         :or {on-preload (fn [this])
              on-create (fn [this])
              on-update (fn [this time delta])}} options]
    (set! (.. scene -preload)
          (fn [] (this-as this (on-preload this))))
    (set! (.. scene -create)
          (fn [] (this-as this (on-create this))))
    (set! (.. scene -update)
          (fn [time delta] (this-as this (on-update this time delta))))
    scene))
