(require '[aql.util :as ut] :reload)

(def foo [5 6 7 8])
(def bar [:a :b :c])

(ut/spiral 4 1 foo)
;; ((5 6 7 8) (6 7 8 5) (7 8 5 6) (8 5 6 7))
(ut/spiral 4 2 foo)
;; ((5 6 7 8) (7 8 5 6))
(ut/spiral 6 1 foo)
;; ((5 6 7 8 5 6) (6 7 8 5 6 7) (7 8 5 6 7 8) (8 5 6 7 8 5))
(ut/spiral 6 2 bar foo)
;; ((5 6 7 8 :a :b) (7 8 :a :b :c :a))
