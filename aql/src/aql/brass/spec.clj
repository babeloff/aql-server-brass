
(ns aql.brass.spec
  (:require [clojure.spec.alpha :as s]
            [aql.spec :as aql]))


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

(s/def ::column (s/coll-of string? :kind vector? :distinct true))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::aql/name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::schema-perturbation (s/keys :req [::tables]))
