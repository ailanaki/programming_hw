(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)
(defn _empty [value] (partial -return value))
(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))
(defn _map [f result]
  (if (-valid? result)
    (-return (f (-value result)) (-tail result))))
(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        (_map (partial f (-value ar))
              ((force b) (-tail ar)))))))
(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))
(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(defn +char [chars] (_char (set chars)))
(defn +map [f parser] (comp (partial _map f) parser))
(def +ignore (partial +map (constantly 'ignore)))
(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))
(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))
(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))
(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))
(defn +or [p & ps]
  (reduce _either p ps))
(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))
(defn +plus [p] (+seqf cons p (+star p)))
(defn +str [p] (+map (partial apply str) p))
(defn +opt [p]
  (+or p (_empty nil)))
(def *digit (+char "0123456789"))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))
(defn proto-get [obj key]
  (cond
    (contains? obj key) (obj key)
    (contains? obj :prototype) (proto-get (obj :prototype) key)
    :else nil))
(defn proto-call [this key & args]
  (apply (proto-get this key) this args))
(defn field [key]
  (fn [this] (proto-get this key)))
(defn method [key]
  (fn [this & args] (apply proto-call this key args)))
(def a (field :_a))
(def b (field :_b))
(def s (field :_s))
(def f (method :_f))
(def d (method :_d))
(def diff (method :_diff))
(def evaluate (method :_evaluate))
(def toString (method :_toString))
(def toStringSuffix (method :_toStringSuffix))
(defn constructor [ctor prototype]
  (fn [& args] (apply ctor {:prototype prototype} args)))
(defn OperationBinary [f, sign, d] (constructor
                                        (fn [this a b]
                                          (assoc this
                                            :_a a
                                            :_b b
                                            )
                                          )
                                        {
                                         :_toString (fn [this] (clojure.string/join "" (vector "(" (s this) " " (str (toString (a this))) " " (str (toString (b this))) ")")))
                                         :_evaluate (fn [this x] (f this (double (evaluate (a this) x)) (double (evaluate (b this) x))))
                                         :_f        f
                                         :_s        sign
                                         :_diff (fn [this x] (d (a this) (b this) (diff (a this) x) (diff (b this) x)))
                                         :_d d
                                         :_toStringSuffix (fn [this] (clojure.string/join "" (vector "(" (str (toStringSuffix (a this))) " " (str (toStringSuffix (b this)))  " " (s this) ")")))
                                         }))
(defn OperationUnary [f, sign, d] (constructor
                                       (fn [this a]
                                         (assoc this
                                           :_a a))
                                       {:_toString (fn [this] (clojure.string/join "" (vector "(" (s this) " " (str (toString (a this))) ")")))
                                        :_evaluate (fn [this x] (f this (evaluate (a this) x)))
                                        :_f        f
                                        :_s        sign
                                        :_diff (fn [this x] (d (a this)  (diff (a this) x)))
                                        :_d d
                                        :_toStringSuffix (fn [this] (clojure.string/join "" (vector "(" (str (toStringSuffix (a this))) " " (s this) ")")))})
  )
(defn constrVarConst [evaluate, diff] (constructor
                                        (fn [this a]
                                          (assoc this
                                            :_a a))
                                        {:_toString (fn [this] (if (number? (a this))
                                                                 (format "%.1f" (double (a this)) )
                                                                 (a this)))
                                         :_evaluate evaluate
                                         :_diff diff
                                         :_toStringSuffix  (fn [this] (if (number? (a this))
                                                                        (format "%.1f" (double (a this)) )
                                                                        (a this)))
                                         }))
(def Add (OperationBinary (fn [this a b] (+ a b))
                          '+
                          (fn [x y dx dy] (Add dx dy))))
(def Subtract (OperationBinary (fn [this a b] (- a b))
                               '-
                               (fn [x y dx dy] (Subtract dx dy))))
(def Multiply (OperationBinary (fn [this a b] (* a b))
                               '*
                               (fn [x y dx dy] (Add (Multiply x dy) (Multiply y dx)))))
(def Divide (OperationBinary (fn [this a b] (/ (double a) (double b)))
                             '/
                             (fn [x y dx dy] (Divide (Subtract (Multiply y dx) (Multiply x dy)) (Multiply y y)))))
(def Constant (constrVarConst
                (fn [this x] (a this))
                (fn [this x] (Constant 0))))
(def Variable (constrVarConst
                (fn [this x] (x  (clojure.string/lower-case (get (a this) 0)) ))
                (fn [this x] (if (= (clojure.string/lower-case (get (a this) 0)) x) (Constant 1) (Constant 0)))))
(def Negate (OperationUnary (fn [this a] (- a))
                            'negate
                            (fn [x dx] (Negate dx))))
(def Exp (OperationUnary
           (fn [this a] (Math/exp a))
           'exp
           (fn [x dx] (Multiply dx (Exp x)))))
(def Ln (OperationUnary
          (fn [this a] (Math/log (Math/abs a)))
          'ln
          (fn [x dx](Divide dx x))))
(def func {
           "+" Add, "-" Subtract, "*" Multiply, "/" Divide, "negate" Negate, "exp" Exp, "ln" Ln
           })


(def *number (+map read-string (+seqf str (+opt (+char "-"))(+str (+plus *digit)) (+opt (+seqf str (+char ".") (+str (+plus *digit)))))))
(def *var (+str (+plus (+char "xyzXYZ"))))
(def *constant (+map Constant *number))
(def *variable (+map Variable *var))
(defn +type [a] (func (str a)))
(def *operation (+map +type (+or (+char "+-*/") (+str (+star (+char "negate"))))))
(def *varConst (+seqn 0 *ws (+or *variable *constant) *ws))
(defn operationReverse  [list]  (apply (first list) (reverse (rest list))))
(def *parser
  (let [*value (delay (+map operationReverse (+map reverse (+or
                                                              (+seq *ws *parser *ws *parser *ws *operation *ws)
                                                              (+seq *ws *parser *ws *operation *ws)
                                                              ))))]
     (+seqn 0 *ws (+or *varConst
                      (+seqn 1
                             (+char "(")
                             *value
                             (+char ")"))) *ws)))
(defn parseObjectSuffix [string] (-value (*parser string)))
