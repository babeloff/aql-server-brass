;;
;; Schema for demonstrating the BRASS approach
;;

(ns aql.example.data)

(def schema-s
  {:name "S"
   :type :schema
   :extend "sql1"
   :entities
   #{"a" "b"}
   :attributes
   {"i" ["a" "Varchar"]
    "j" ["a" "Varchar"]

    "k"  ["b" "Varchar"]
    "m"  ["b" "Varchar"]}
   :references
   {"a_fk" ["b" "a"]}})

(def schema-x
  {:name "X"
   :type :schema
   :extend "sql1"
   :entities
   #{["a" "c"]
     ["a" "d"]
     ["b" "c"]
     ["b" "d"]}
   :attributes
   {"i" [["a" "c"] "Varchar"]
    "j" [["a" "d"] "Varchar"]

    "k"  [["b" "c"] "Varchar"]
    "m"  [["b" "d"] "Varchar"]}

   :references
   {"a_fk" [["a" "c"]]}})


(def schema-t
  {:name "T"
   :type :schema
   :extend "sql1"
   :entities
   #{"c" "d"}
   :attributes
   {"i" ["a" "Varchar"]
    "j" ["a" "Varchar"]

    "k"  ["b" "Varchar"]
    "m"  ["b" "Varchar"]}
   :references
   {"a_fk" ["b" "a"]}})


(def m-x->s
  {:name "F"
   :type :mapping
   :schemas ["X" "S"]
   :entities
   {[["a" "c"] ["a"]] {:attributes {"i" "i"}}
    [["a" "d"] ["a"]] {:attributes {"j" "j"}}
    [["b" "c"] ["b"]] {:attributes {"k" "k"}}
    [["b" "d"] ["b"]] {:attributes {"m" "m"}}}})

(def m-x->t
  {:name "G"
   :type :mapping
   :schemas ["X" "T"]
   :entities
   {[["a" "c"] ["c"]] {:attributes {"i" "i"}}
    [["a" "d"] ["d"]] {:attributes {"j" "j"}}
    [["b" "c"] ["c"]] {:attributes {"k" "k"}}
    [["b" "d"] ["d"]] {:attributes {"m" "m"}}}})

(def q-m "
  query Q = literal : S -> T {
     entity c -> {
       from ca:a cb:b
       attributes
         i -> i(ca)
         k -> k(cb)
       foreign_keys
         // d_fk : c -> d
         d_fk -> {
           ca -> da
           cb -> db}}
     entity d -> {
       from da:a db:b
       attributes
         j -> j(da)
         m -> m(db)
       foreign_keys
         // c_fk : d -> c
         c_fk -> {
           da -> ca
           db -> cb}}")
