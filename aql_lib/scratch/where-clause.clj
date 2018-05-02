(require ' (instaparse [core :as insta]) :reload)

;; "convert something like this..
(def sam-ob "OrBool(EqualVc(v12.channel, '5'), EqualVc(ce.cot_type, 'a-n-A-C-F-s')) = 'true'")
(def sam-ev "EqualVc(v12.channel, '5')")
(def sam-term1 "'25'")
(def sam-term2 "v12.channel")
;; ...into...
(def res "v12.channel = '5' OR ce.cot_type = 'a-n-A-C-F-s'")

(def ob-gram (insta/parser (clojure.java.io/resource "or_bool.bnf")))
(insta/parses ob-gram sam-ob)
(def ev-gram (insta/parser (clojure.java.io/resource "eq_vc.bnf")))
(insta/parses ev-gram sam-ev)
(def term-gram (insta/parser (clojure.java.io/resource "term.bnf")))
(insta/parses term-gram sam-term1)
(insta/parses term-gram sam-term2)
