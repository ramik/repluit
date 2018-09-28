(ns repluit.impl.retry
  (:require [repluit.impl.exception :as ex]))

(defn try-run [f]
  (try
    {:value (f)}
    (catch Exception e
      (if-not (ex/retryable? e)
        (throw e))
      {:error e})))

(defn loop* [f timeout expr-form]
  (loop [start (System/currentTimeMillis)]
    (let [result (try-run f)]
      (if (:value result)
        result
        (do (when (> (- (System/currentTimeMillis) start) timeout)
              (let [msg    "Could not satisfy expression withing given time frame"
                    reason (or (:error result)
                               (str "Value was: "
                                    (pr-str (:value result))))]
                (throw (ex/timeout msg expr-form reason))))
            (Thread/sleep 50)
            (recur start))))))

