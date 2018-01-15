  
(ns aql.brass.util
    (:require 
        (clojure [pprint :as pp]
                 [string :as st])
        (com.rpl [specter :as sr]))
    (:import 
        (catdata.aql 
            AqlCmdLine)
        (catdata.aql.exp 
            AqlEnv
            AqlParser 
            AqlMultiDriver)))
            
(defn expand-perturbation 
    "construct an sequence of tuples [new-entity old-entity column]"
    [pert] 
    (->> pert
        (sr/select [:tables sr/ALL (sr/collect-one :name)
                    :columns sr/ALL (sr/collect-one sr/FIRST) 
                    sr/LAST])
        (map (fn [[new-ent old-ent col-name]]
                [col-name [old-ent new-ent]]))
        (into {})))
            
                    

(defn schema-map-by-name
    [base]
    (->> base
        (sr/select 
            [   (sr/submap [:references :attributes]) sr/ALL (sr/collect-one sr/FIRST) 
                sr/LAST sr/ALL (sr/collect-one sr/FIRST) 
                sr/LAST (sr/collect-one sr/FIRST) 
                sr/LAST])
        (map (fn [[arrow-type col-name ent-name col-type]]
                [col-name { :atype arrow-type
                            :ent-name ent-name
                            :col-type col-type}]))
        (into {})))           
        
(defn aql-factory
    [base pert]
    (let [  ent-map (schema-map-by-name base)
            arrows (expand-perturbation pert)
            col-map (merge-with #(conj %1 [:move %2]) ent-map arrows)
            ent-x (->> arrows (sr/select [sr/MAP-VALS]) distinct)
            ent-s (->> arrows (sr/select [sr/MAP-VALS sr/FIRST]) distinct)
            ent-t (->> arrows (sr/select [sr/MAP-VALS sr/LAST]) distinct)]
        {   :x 
            {   :name "X"
                :type :schema
                :entend "sql1" 
                :entities (into #{} ent-x) 
                :attributes 
                (->> col-map 
                    (filter (fn [[_ {atype :atype}]] (= atype :attributes)))
                    (map 
                        (fn [[col-name {ent :move, col-type :col-type}]] 
                            [col-name [ent col-type]])))                     
                :references 
                (->> col-map 
                    (filter (fn [[_ {atype :atype}]] (= atype :references)))
                    (map 
                        (fn [[col-name {ent :move, col-type :col-type}]] 
                            [col-name [ent col-type]])))}
            :t
            {   :name "T" 
                :type :schema
                :entend "sql1" 
                :entities (into #{} ent-t) 
                :attributes 
                (->> col-map 
                    (filter (fn [[_ {atype :atype}]] (= atype :attributes)))
                    (map 
                        (fn [[col-name {[old-ent new-ent] :move, col-type :col-type}]] 
                            [col-name [new-ent col-type]])))                     
                :references 
                (->> col-map 
                    (filter (fn [[_ {atype :atype}]] (= atype :references)))
                    (map 
                        (fn [[col-name {[old-ent new-ent] :move, col-type :col-type}]] 
                            [col-name [new-ent col-type]])))}
            :f
            {   :name "F" 
                :type :mapping
                :schemas ["X" "S"] 
                :entities (-> ent-x) 
                :attributes nil
                :references nil}
            :g
            {   :name "G" 
                :type :mapping
                :schemas ["X" "T"] 
                :entities (-> ent-x) 
                :attributes nil
                :references nil}}))
            
             
