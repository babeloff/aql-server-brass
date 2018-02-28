
(ns aql.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::type #{::schema ::mapping ::query})
(s/def ::entity-key (s/or :single string? :multi (s/coll-of string? :kind vector?)))

(s/def ::extend string?)
(s/def ::entities (s/coll-of ::entity-key :kind set? :distinct true))
(s/def ::attribute (s/tuple ::entity-key string?))
(s/def ::attributes (s/map-of string? ::attribute))
(s/def ::reference (s/tuple ::entity-key string?))
(s/def ::references (s/map-of string? ::reference))
(s/def ::alias (s/+ (s/cat :a string? :r string?)))
(s/def ::relation #{::equal ::lt})
(s/def ::expression (s/coll-of string? :kind vector?))
(s/def ::path (s/tuple ::relation ::expression ::expression))
(s/def ::paths (s/coll-of ::path :kind vector?))
(s/def ::observation (s/tuple ::alias (s/tuple ::relation
                                               ::expression
                                               ::expression)))
(s/def ::observations (s/coll-of ::observation :kind vector?))
(s/def ::schema (s/keys :req [::name ::type ::extend ::entities]
                        :opt [::attributes ::references
                              ::paths ::observations]))

(s/def ::attribute-map (s/map-of string? string?))
(s/def ::reference-map (s/map-of string? (s/or :e nil? :r string?)))
(s/def ::entity-prop-map (s/keys :req [::attribute-map] :opt [::reference-map]))
(s/def ::entity-key-map (s/tuple ::entity-key ::entity-key))
(s/def ::entity-map (s/map-of ::entity-key-map ::entity-prop-map))
(s/def ::schema-map (s/tuple string? string?))
(s/def ::mapping (s/keys :req [::name ::type ::schema-map ::entity-map]))
