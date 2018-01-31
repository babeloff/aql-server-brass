
(ns aql.brass.spec
  (:require [clojure.spec.alpha :as s]))


(def schema-permutation-mapping
  {"Source_Name" {:ent "source" :col "name"}
   "Source_Channel" {:ent "source" :col "channel"}

   "CotEvent_SourceId" {:ent "cot_event" :col "source_id"}
   "CotEvent_CotType" {:ent "cot_event" :col "cot_type"}
   "CotEvent_How" {:ent "cot_event" :col "how"}
   "CotEvent_Detail" {:ent "cot_event" :col "latitude"}
   "CotEvent_ServerTime" {:ent "cot_event" :col "servertime"}

   "Position_CotEventId" {:ent "cot_event_position" :col "cot_event_id"}
   "Position_PointHae" {:ent "cot_event_position" :col "point_hae"}
   "Position_PointCE" {:ent "cot_event_position" :col "point_ce"}
   "Position_PointLE" {:ent "cot_event_position" :col "point_le"}
   "Position_TileX" {:ent "cot_event_position" :col "tileX"}
   "Position_TileY" {:ent "cot_event_position" :col "tileY"}
   "Position_Longitude" {:ent "cot_event_position" :col "longitude"}
   "Position_Latitude" {:ent "cot_event_position" :col "latitude"}})

(s/def ::name string?)
(s/def ::column (s/coll-of string? :kind vector? :distinct true))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::schema-perturbation (s/keys :req [::tables]))



(s/def ::type #{:schema :mapping :query})
(s/def ::extend string?)
(s/def ::entity (s/or :single string? :multi (s/coll-of string?)))
(s/def ::entities (s/coll-of ::entity :kind set? :distinct true))
(s/def ::attribute (s/tuple ::entity string?))
(s/def ::attributes (s/map-of string? ::attribute))
(s/def ::reference (s/tuple ::entity string?))
(s/def ::references (s/map-of string? ::attribute))
(s/def ::schema (s/keys :req [::name ::type ::extend ::entities]
                        :opt [::attributes ::references]))
