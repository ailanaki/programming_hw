(defn doMath [f] (fn [a b] (mapv f a b)))
(def v+ (doMath +))
(def v- (doMath -))
(def v* (doMath *))
(def vd (doMath /))
(defn v*s [a b] (mapv (fn [z] (* z b)) a))

(defn dosmth [a b y z] (- (* (get a y) (get b z)) (* (get a z) (get b y))))
(defn scalar [a b] (apply + (v* a b)))
(defn vect [a b] (vector (dosmth a b 1 2) (dosmth a b 2 0) (dosmth a b 0 1)))


(def m+ (doMath v+))
(def m- (doMath v-))
(def m* (doMath v*))
(def md (doMath vd))
(defn m*s [a b] (mapv (fn [t] (v*s t b)) a))
(defn m*v [a b] (mapv (fn [t] (apply + t)) (mapv (fn [t] (v* t b)) a)))
(defn transpose [a]  (apply mapv vector a))
(defn m*m [a b] (transpose (mapv (fn [t] (m*v a t)) (transpose b))) )

(def c+ (doMath m+))
(def c- (doMath m-))
(def c* (doMath m*))
(def cd (doMath md))