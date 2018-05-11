(ns aql.example.data-cospan
  (:require
   (clojure [pprint :as pp]
            [string :as st])
   (com.rpl [specter :as sr])
   (aql [spec :as asp])))

(def schema-s
  #::asp
  {:name "S"
   :type ::asp/schema
   :extend "sql1"
   :entities
   #{"a" "b"}
   :attributes
   {"i" ["a" "Varchar"]
    "j" ["a" "Varchar"]

    "k"  ["b" "Varchar"]
    "m"  ["b" "Varchar"]}
   :references
   {"has" ["b" "a"]}})

(def schema-t
  #::asp
  {:name "T"
   :type ::asp/schema
   :extend "sql1"
   :entities
   #{"c" "d"}
   :attributes
   {"i" ["c" "Varchar"]
    "j" ["d" "Varchar"]

    "k"  ["c" "Varchar"]
    "m"  ["d" "Varchar"]}
   :references
   {"c_fk" ["d" "c"]
    "d_fk" ["c" "d"]}})

(def schema-r "schema R = T")

(def schema-x
  #::asp
  {:name "X"
   :type ::asp/schema
   :extend "sql1"
   :entities
   #{"n"}
   :attributes
   {"i" ["n" "Varchar"]
    "j" ["n" "Varchar"]

    "k"  ["n" "Varchar"]
    "m"  ["n" "Varchar"]}})

(def mapping-f
  #::asp
  {:name "F"
   :type ::asp/mapping
   :schema-map ["S" "X"]
   :entity-map
   {[["a"] ["n"]]
    #::asp
    {:attribute-map
     {"i" "i"
      "j" "j"}}

    [["b"] ["n"]]
    #::asp
    {:reference-map {"has" nil}
     :attribute-map
     {"k" "k"
      "m" "m"}}}})

(def mapping-g
  #::asp
  {:name "G"
   :type ::asp/mapping
   :schema-map ["T" "X"]
   :entity-map
   {[["c"] ["n"]]
    #::asp
    {:reference-map {"d_fk" nil}
     :attribute-map
     {"i" "i"
      "k" "k"}}

    [["d"] ["n"]]
    #::asp
    {:reference-map {"c_fk" nil}
     :attribute-map
     {"j" "j"
      "m" "m"}}}})

(def instance-js
  "instance Js = literal : S {
   generators
     a1 a2 a3 a4 a5 : a
     b1 b2 b3 b4 b5a b5b : b
   equations
    a1.i = \"a1i\" a1.j = \"a1j\"
    b1.has = a1 b1.k = \"b1k\" b1.m = \"b1m\"
    a2.i = \"a2i\" a2.j = \"a2j\"
    b2.has = a2 // b2.k = \"b2k\" b2.m = \"b2m\"

    a3.i = \"a3i\" a3.j = \"a3j\"
    b3.has = a3 b3.k = \"b3k\" b3.m = \"b3m\"
    a4.i = \"a4i\" a4.j = \"a4j\"
    b4.has = a4 // b4.k = \"b4k\" b4.m = \"b4m\"

    a5.i = \"a5i\" a5.j = \"a5j\"
    b5a.has = a5 b5a.k = \"b5ak\" b5a.m = \"b5am\"
    b5b.has = a5 b5b.k = \"b5bk\" b5b.m = \"b5bm\"
  }")

(def instance-jx "instance Jx = pi F Js")
(def instance-jy "instance Jy = sigma F Js
  { options require_consistency=false allow_java_eqs_unsafe=true }")

(def instance-jx-goal
 "instance JxGoal = literal : X {
  generators
    x1 x2 x3 x4 x5a x5b : n
  equations
    x1.i = \"a1i\" x1.j = \"a1j\" x1.k = \"b1k\" x1.m = \"b1m\"
    x2.i = \"a2i\" x2.j = \"a2j\"
    x3.i = \"a3i\" x3.j = \"a3j\" x3.k = \"b2k\" x3.m = \"b2m\"
    x4.i = \"a4i\" x4.j = \"a4j\"

    x5a.i = \"a5i\" x5a.j = \"a3j\" x5a.k = \"b3k\" x5a.m = \"b3m\"
    x5b.i = \"a5i\" x5b.j = \"a3j\" x5b.k = \"b4k\" x5b.m = \"b4m\"
  }")

(def instance-jt
 "instance Jt = delta G Jx // JxGoal")
; instance Jt = delta G sigma F Js
;  { options require_consistency=false allow_java_eqs_unsafe=true}

; the JtSigma is isomorphic with the target JtGoal
(def instance-jt-goal
 "instance JtGoal = literal : T {
  generators
    c1 c2 c3 c4 : c
    d1 d2 d3 d4 : d
  equations
    c1.i = \"a1i\" c1.k = \"b1k\" c1.d_fk = d1
    c2.i = \"a2i\"              c2.d_fk = d2
    c3.i = \"a3i\" c3.k = \"b2k\" c3.d_fk = d3
    c4.i = \"a4i\"              c4.d_fk = d4

    d1.j = \"a1j\" d1.m = \"b1m\" d1.c_fk = c1
    d2.j = \"a2j\"              d2.c_fk = c2
    d3.j = \"a3j\" d3.m = \"b2m\" d3.c_fk = c3
    d4.j = \"a4j\"              d4.c_fk = c4
  }")

(def query-qf "query QF = toQuery F")
(def query-qg "query QG = toCoQuery G")
(def query-qx "query Qx = [ QG ; QF ]")

(def instance-js-rt "instance JsRT = eval Qx JtGoal")
;;  JsRT should be isomorphic with Js

(def query-qs-a
 "query Qs_a = literal : S -> R {
  entity c -> {
  	from B1: b
    attributes
  	  i -> B1.has.i
  	  k -> B1.k

    foreign_keys
  	  d_fk -> {
  	    B2 -> B1
  	  }
  }
  entity d -> {
  	from
	   B2 : b
    attributes
  	  j -> B2.has.j
  	  m -> B2.m
    foreign_keys
  	  c_fk -> {
  	    B1 -> B2
  	  }
  }
}")

(def instance-ks-a "instance Ks_a = eval Qs_a Js")
(def query-qt-a "query Qt_a = [ Qx ; Qs_a ]")
(def instance-kt-a "instance Kt_a = eval Qt_a JtGoal")

(def query-qs-b
 "query Qs_b = literal : S -> R {
 entity c -> {
   from ca:a cb:b
   attributes
     i -> i(ca)
     k -> k(cb)
   foreign_keys
     // d_fk : c -> d
     d_fk -> {da -> ca   db -> cb}
    }
 entity d -> {
   from da:a db:b
   attributes
     j -> j(da)
     m -> m(db)
   foreign_keys
     // c_fk : d -> c
     c_fk -> {ca -> da   cb -> db}
 }
}")

(def instance-ks-b "instance Ks_b = eval Qs_b Js")
(def query-qt-b "query Qt_b = [ Qx ; Qs_b ]")
(def instance-kt-b "instance Kt_b = eval Qt_b JtGoal")

; instance Kt_b_std = relationalize Kt_b
; This reminds me of working with the hyper-reals.
; When converting a hyper-real to a real only
; the "standard" portion of the value is retained.

(def schema-r2
  #::asp
  {:name "X"
   :type ::asp/schema
   :extend "sql1"
   :entities
   #{"c"}
   :attributes
   {"i" ["c" "Varchar"]
    "j" ["c" "Varchar"]

    "k"  ["c" "Varchar"]
    "m"  ["c" "Varchar"]}})


(def query-qs-1
 "query Qs_1 = literal : S -> R2 {
  entity c -> {
  	from B1: b
    where
      B1.k  = \"b1k\"
    attributes
      i -> B1.has.i
      j -> B1.has.j
      k -> B1.k
      m -> B1.m
  }
}")

(def instance-ks-1 "instance Ks_1 = eval Qs_1 Js")
(def query-qs-2pre
 "query Qs_2pre = literal : S -> R2 {
  params
    kay : Varchar

  entity c -> {
  	from B1: b
    where
     B1.k = kay
    attributes
      i -> B1.has.i
      j -> B1.has.j
      k -> B1.k
      m -> B1.m
  }
}
")

(def query-qs-2
 "query Qs_2 = literal : S -> R2 {
  bindings
    kay = \"b1k\"
  imports
    Qs_2pre
}")

(def instance-kx-2 "instance Ks_2 = eval Qs_2 Js")
