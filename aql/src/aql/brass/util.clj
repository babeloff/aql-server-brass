
(ns aql.brass.util
    (:require 
        (clojure [pprint :as pp]
                 [string :as st])
        (aql.brass [util :as brass])))

(defn build-aql-attr [column])
     
(defn build-aql [schema-mapping]
    (let [pert (get-in schema-mapping 
                    ["martiServerModel" 
                     "requirements"
                     "postgresqlPerturbation"])]
    
        {:name "S1"}
        :type :schema
        :extend "sql" 
        :entities 
            (into #{} (get-in pert ["tables" "name"]))
        :attributes 
            (into {} (get-in pert))))

        
