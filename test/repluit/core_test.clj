(ns repluit.core-test
  (:require [clojure.test :refer :all]
            [repluit.test :refer [is*]]
            [repluit.core :as r :refer [do!]]))

(def headless? true)

(defn ui-test-suite-fixture [test]
  (with-open [sess    (r/new-session! {:width 1000 :height 1000})
              browser (r/open-browser! sess {:headless headless?})]
    (binding [r/*browser* browser
              r/*config*  (assoc r/*config* :typing-speed :tycitys)]
      (test))))

(defn ui-test-case-fixture [test]
  (r/goto! "file:///Users/matti/dev/repluit/examples/todomvc/example.html")
  (test))

(use-fixtures :once ui-test-suite-fixture)
(use-fixtures :each ui-test-case-fixture)

(defn new-todo-input []
  (r/q "#new-todo"))

(defn clear-completed-button []
  (r/q "#clear-completed"))

(defn todo-items []
  (r/q "#todo-list > li"))

(defn todo-text [todo]
  (r/text-content todo))

(defn todo-completed? [todo]
  (r/has-class? todo "completed"))

(defn todo-complete-toggle [todo]
  (r/q todo ".toggle"))

(defn todo-by-text [text]
  (->> (todo-items)
       (filter #(= text (todo-text %)))
       (first)))

(defn add-todo! [text]
  (doto (new-todo-input)
    (r/clear-text!)
    (r/type! text :enter)))

(defn remove-todo! [todo]
  (r/hover! todo)
  (-> (r/q todo "button.destroy")
      (r/click!)))

(defn toggle-complete-status! [todo]
  (r/click! (todo-complete-toggle todo)))

(deftest adding-new-todo
  (testing "new todo items are displayed on the list"
    (is* (= (empty? (todo-items))))
    (do! (add-todo! "lol")
         (add-todo! "bal"))
    (is* (= ["lol" "bal"] (map todo-text (todo-items))))))

(deftest removing-todo
  (testing "todo is removed from the list"
    (do! (add-todo! "lol")
         (add-todo! "bal"))
    (is* (= 2 (count (todo-items))))
    (do! (remove-todo! (first (todo-items))))
    (is* (= 1 (count (todo-items))))))

(deftest marking-todo-as-completed-and-removing-it
  (testing "completed todos can be removed by clicking 'clear completed' button"
    (do! (add-todo! "lol")
         (add-todo! "bal")
         (add-todo! "foo"))
    (is (= [false false false] (map todo-completed? (todo-items))))
    (is* (not (r/visible? (clear-completed-button))))
    (do! (toggle-complete-status! (todo-by-text "bal")))
    (is* (= [false true false] (map todo-completed? (todo-items))))
    (is* (r/visible? (clear-completed-button)))
    (do! (r/click! (clear-completed-button)))
    (is* (= ["lol" "foo"] (map todo-text (todo-items))))))

