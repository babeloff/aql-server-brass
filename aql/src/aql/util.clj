
(ns aql.util
    (:require 
        (clojure [pprint :as pp]
                 [string :as st]))
    (:import 
        (catdata.aql.exp 
            AqlEnv
            AqlParser 
            AqlMultiDriver)))

;; It may be useful to pass in an array to update
;; see src/catdata/aql/gui/AqlCodeEditor:: makeEnv

(defn mapjoin [delimiter f col]
    (st/join delimiter (map f col)))

(defn wrap-schema [schema literal]
    (str "schema " (:name schema) 
        " = literal : " (:extend schema) 
        " {\n" literal "\n}\n"))
            
(defn serial-aql-schema [schema]
    (wrap-schema schema 
        (str 
            " entities " "\n"
            (st/join " " (:entities schema)) 
            "\n"
            " foreign_keys " "\n"
            (mapjoin " " 
                (fn [[key [src dst]]] 
                    (str key " : " src " -> " dst)) 
                (:references schema)) 
            "\n"                         
            " path_equations " "\n"
            (mapjoin " " 
                (fn [[left right]] 
                    (str (st/join "." left) " = " (st/join "." right))) 
                (:equations schema))
            "\n"
            " attributes " "\n"
            (mapjoin " " 
                (fn [[key [src type]]] 
                    (str key " : " src " -> " type)) 
                (:attributes schema)))))
              


        
