(ns repluit.impl.exception
  (:import (repluit ExecutionException WaitTimeoutException)))

(defn- eex-data [ex]
  (if (instance? ExecutionException ex)
    (.-data ex)))

(defn retryable [^String msg & [data]]
  (ExecutionException. msg true data))

(defn timeout [expr reason]
  (-> (str "Timeout when waiting for expression: " expr
           "\n\nReason: " reason)
      (WaitTimeoutException.)))

(defn js-error [description]
  (ExecutionException. "JavaScript error" false {:description description}))

(defn stale-node []
  (retryable "Can't use node because it's already removed from the DOM" {::type :stale}))

(defn node-not-visible []
  (retryable "DOM node is not visible" {::type :not-visible}))

(defn retryable? [ex]
  (and (instance? ExecutionException ex)
       (true? (.-retryable ex))))

(defn stale-node? [ex]
  (= :stale (::type (eex-data ex))))

(defmacro safely [expr]
  `(try ~expr (catch ExecutionException e# nil)))

(defmacro with-stale-ignored [expr]
  `(try
     ~expr
     (catch ExecutionException e#
       (if-not (stale-node? e#) (throw e#))
       nil)))
