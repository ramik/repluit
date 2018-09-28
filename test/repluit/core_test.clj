(ns repluit.core-test
  (:require [clojure.test :refer [deftest testing use-fixtures]]
            [repluit.test :refer [is]]
            [repluit.core :as t]
            [repluit.dev :as dev]))

(def headless? true)

(defn ui-test-suite-fixture [test]
  (with-open [sess    (t/new-session! {:width 1000 :height 1000})
              browser (t/open-browser! sess {:headless headless?})]
    (binding [t/*browser* browser
              t/*config*  (assoc t/*config* :typing-speed :tycitys)]
      (test))))

(defn ui-test-case-fixture [test]
  (t/goto! "file:///Users/matti/dev/repluit/examples/todomvc/example.html")
  (test))

(use-fixtures :once ui-test-suite-fixture)
(use-fixtures :each ui-test-case-fixture)

(defn new-todo-input []
  (t/q "#new-todo"))

(defn clear-completed-button []
  (t/q "#clear-completed"))

(defn todo-items []
  (t/q "#todo-list > li"))

(defn todo-text [todo]
  (t/text-content todo))

(defn todo-completed? [todo]
  (t/has-class? todo "completed"))

(defn todo-complete-toggle [todo]
  (t/q todo ".toggle"))

(defn todo-by-text [text]
  (->> (todo-items)
       (filter #(= text (todo-text %)))
       (first)))

(defn add-todo! [text]
  (doto (new-todo-input)
    (t/clear-text!)
    (t/type! text :enter)))

(defn remove-todo! [todo]
  (t/hover! todo)
  (-> (t/q todo "button.destroy")
      (t/click!)))

(defn toggle-complete-status! [todo]
  (t/click! (todo-complete-toggle todo)))

(deftest adding-new-todo
  (testing "new todo items are displayed on the list"
    (is (= (empty? (todo-items))))
    (t/with-retry
      (add-todo! "lol")
      (add-todo! "bal"))
    (is (= ["lol" "bal"] (map todo-text (todo-items))))))

(deftest removing-todo
  (testing "todo is removed from the list"
    (t/with-retry
      (add-todo! "lol")
      (add-todo! "bal"))
    (is (= 2 (count (todo-items))))
    (t/with-retry (remove-todo! (first (todo-items))))
    (is (= 1 (count (todo-items))))))

(deftest marking-todo-as-completed-and-removing-it
  (testing "completed todos can be removed by clicking 'clear completed' button"
    (t/with-retry
      (add-todo! "lol")
      (add-todo! "bal")
      (add-todo! "foo"))
    (is (= [false false false] (map todo-completed? (todo-items))))
    (is (not (t/visible? (clear-completed-button))))
    (t/with-retry (toggle-complete-status! (todo-by-text "bal")))
    (is (= [false true false] (map todo-completed? (todo-items))))
    (is (t/visible? (clear-completed-button)))
    (t/with-retry (t/click! (clear-completed-button)))
    (is (= ["lol" "foo"] (map todo-text (todo-items))))))



(comment
  (def sess (t/new-session! {:width 1000 :height 1000}))
  (def browser (t/open-browser! sess {:headless false}))

  (dev/set-browser! browser)

  (dev/update-config! assoc :timeout 1000)

  (t/goto! "file:///Users/matti/dev/repluit/examples/todomvc/example.html")

  (t/q "div")
  (add-todo! "adsad")

  (t/wait (t/outer-html (todo-items)))


  (t/close! browser)



  )
