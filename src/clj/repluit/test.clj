(ns repluit.test
  (:require [clojure.test :as t]
            [repluit.core :refer [wait]]))

(defmacro is [form]
  `(let [rep#   (atom nil)
         report (fn [_] (some-> @rep# (t/do-report)))]
     (try
       (doto (with-redefs [t/do-report #(reset! rep# %)] (wait (is ~form)))
         (report))
       (catch Throwable t#
         (report)
         nil))))
