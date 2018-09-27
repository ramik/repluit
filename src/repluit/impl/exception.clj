(ns repluit.impl.exception)

(defn retryable [msg & [data]]
  (ex-info msg (assoc (or data {}) ::retryable true)))

(defn timeout [msg expr reason]
  (ex-info msg {:reason     reason
                :expression expr}))

(defn js-error [description]
  (ex-info "JavaScript error" {:description description}))

(defn stale-node []
  (retryable "Can't use node because it's already removed from the DOM" {::type :stale}))

(defn node-not-visible []
  (retryable "DOM node is not visible" {::type :not-visible}))

(defn retryable? [ex]
  (true? (::retryable (ex-data ex))))

(defn stale-node? [ex]
  (= :stale (::type (ex-data ex))))

(defn node-not-visible? [ex]
  (= :not-visible (::type (ex-data ex))))

(defmacro safely [expr]
  `(try ~expr (catch Exception e# nil)))

(defmacro stale=nil [expr]
  `(try
     ~expr
     (catch Exception e#
       (if-not (stale-node? e#) (throw e#))
       nil)))

(defmacro stale=fail [expr]
  expr)
