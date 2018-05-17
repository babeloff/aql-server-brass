
(ns aql.brass.query.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::key string?)
(s/def ::nspace string?)
(s/def ::name string?)
(s/def ::doc string?)
(s/def ::sql string?)
(s/def ::source string?)
(s/def ::select-order (s/coll-of string? :kind vector? :distinct true))
(s/def ::target string?)
