(ns starter.macros)

(defmacro defscene
  [name# & options]
  (let [scene# (gensym)
        options# (apply hash-map options)
        {atoms# :atoms} options#]
    `(defn ~name# []
       (let [~scene# (Phaser/Scene. ~name#)
             ~@atoms#]
         (starter.macros/defscene* ~options# ~scene#)))))
