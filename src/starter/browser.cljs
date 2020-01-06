(ns starter.browser
  (:require [reagent.core :as r]
            [starter.utils :as u]
            ;; js imports
            [phaser :as Phaser]
            [starter.macros :refer [defscene]]
            ["jimp/es"
             :refer [default]
             :rename {default Jimp}]))

(js/console.log Phaser/Curves.MoveTo)


(def FONT 16)
(def ROWS 32)
(def COLS 32)
(def ACTORS 10)



;; GLOBAL STATE
(def state
  (atom
   (let [base-map (u/make-array-2d
                   COLS
                   ROWS
                   (fn [x y]
                     (if (> (js/Math.random) 0.80)
                       "#"
                       ".")))
         ]
     {:map base-map
      :player {}
      :actors []
      :alive []
      :actor-map {}})))

(def screen (u/make-array-2d COLS ROWS (fn [x y] nil)))


;; RENDER FUNCTIONS
(defn render-terrain!
  [state screen]
  (doseq [y (range ROWS)
          x (range COLS)
          :let [chr (aget (state :map) y x)
                cell (aget screen y x)]]
    (.setText cell chr)))

(defn render-actors!
  [state screen]
  (doseq [[i actor] (map vector (range) (:actors state))]
    (if (> (:hp actor) 0)
      (let [cell (aget screen (:y actor) (:x actor))]
        (.setText cell (if (zero? i) "@" "e"))))))

(defn render!
  [state screen]
  (render-terrain! state screen)
  ;; (render-actors! state screen)
  )

(defn draw-char
  [this chr x y]
  (let [style #js {:font (str FONT "px monospace")
                   :fill "#fff"}]
    (.text this.add
           (* FONT 0.6 x)
           (* FONT y)
           chr
           style)))

;; GAME SCENES

;; (def game-scene (Phaser/Scene. "game"))

;; (set! (.. game-scene -preload)
;;       (fn []))

;; (set! (.. game-scene -create)
;;       (fn []
;;         (this
;;           (.read Jimp "./citytemplate.png"
;;                  (fn [err image]
;;                    (let [new-map (u/make-array-2d
;;                                   COLS
;;                                   ROWS
;;                                   (fn [x y]
;;                                     (let [rgba (.intToRGBA
;;                                                 Jimp
;;                                                 (.getPixelColor image x y))]
;;                                       (if (= (.-r rgba) 0) "#" "."))))]
;;                      (swap! state assoc :map new-map)
;;                      (render! @state screen))))
;;           ;; (print @state)
;;           ;; init screen
;;           (doseq [y (range ROWS)
;;                   x (range COLS)]
;;             (aset screen y x (draw-char this "#" x y)))

;;           ;; spawn initial actors
;;           (doseq [e (range ACTORS)
;;                   :let [actor (atom {:x (rand-int COLS)
;;                                      :y (rand-int ROWS)
;;                                      :hp (if (= e 0) 3 1)})]]
;;             ;; generate random positions until we find one that's not
;;             ;; occupied
;;             (while (or (= (aget (:map @state) (:y @actor) (:x @actor)) "#")
;;                        (not (nil? (get (:actor-map @state)
;;                                        {:x (:x @actor) :y (:y @actor)}))))
;;               (swap! actor update :x #(rand-int COLS))
;;               (swap! actor update :y #(rand-int ROWS)))

;;             (swap! state update :actors conj @actor)
;;             (swap! state
;;                    update
;;                    :actor-map
;;                    assoc
;;                    {:x (:x @actor) :y (:y @actor)}
;;                    @actor))

;;           (render! @state screen))))

(defscene game-scene
  :atoms [test (atom nil)]
  :on-preload (fn [this] (reset! test "Hello World!"))
  :on-create (fn [this]

          (.read Jimp "./citytemplate.png"
                 (fn [err image]
                   (let [new-map (u/make-array-2d
                                  COLS
                                  ROWS
                                  (fn [x y]
                                    (let [rgba (.intToRGBA
                                                Jimp
                                                (.getPixelColor image x y))]
                                      (if (= (.-r rgba) 0) "#" "."))))]
                     (swap! state assoc :map new-map)
                     (render! @state screen))))
          ;; (print @state)
          ;; init screen
          (doseq [y (range ROWS)
                  x (range COLS)]
            (aset screen y x (draw-char this "#" x y)))

          ;; spawn initial actors
          (doseq [e (range ACTORS)
                  :let [actor (atom {:x (rand-int COLS)
                                     :y (rand-int ROWS)
                                     :hp (if (= e 0) 3 1)})]]
            ;; generate random positions until we find one that's not
            ;; occupied
            (while (or (= (aget (:map @state) (:y @actor) (:x @actor)) "#")
                       (not (nil? (get (:actor-map @state)
                                       {:x (:x @actor) :y (:y @actor)}))))
              (swap! actor update :x #(rand-int COLS))
              (swap! actor update :y #(rand-int ROWS)))

            (swap! state update :actors conj @actor)
            (swap! state
                   update
                   :actor-map
                   assoc
                   {:x (:x @actor) :y (:y @actor)}
                   @actor))
          (render! @state screen))
  :on-update (fn [this time delta]))

;; GAME INIT

(def config #js {:type Phaser/AUTO
                 :width (* COLS FONT 0.6)
                 :height (* ROWS FONT)
                 :scene game-scene})

(defonce game (Phaser/Game. config))


;; APP BOOT FUNCTIONS

(defn main []
  [:div])

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (r/render [main] (.getElementById js/document "app")))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  ;; (js/document.body.appendChild app.view)
  ;; (set! (.-position app.renderer.view.style) "absolute")
  ;; (set! (.-display app.renderer.view.style) "block")
  ;; (set! (.-autoResize app.renderer) true)
  ;; (.resize app.renderer js/window.innerWidth js/window.innerHeight)
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
