(ns repluit.test
  (:require [clojure.test :refer [is try-expr do-report]]
            [repluit.core :refer [wait]]))

(defmacro is* [form]
  `(let [reports# (atom [])]
     (try
       (let [res# (with-redefs [do-report #(swap! reports# conj %)]
                    (wait (is ~form)))]
         (some-> @reports# (last) (do-report))
         res#)
       (catch Throwable t#
         (some-> @reports# (last) (do-report))
         nil))))
