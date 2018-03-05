
(ns aql.brass.spec
  (:require [clojure.spec.alpha :as s]
            [aql.spec :as aql]))

;; This map contains the information necessary to construct the
;; target schema and the G mapping.
;;
;; The other relevant information is obtained by merging
;; - this map [aql.brass.spec/schema-permutation-mapping]
;;
;; :ent indicates the entity for which is the parent of the attribute
;; :col is the original name for that attribute
;; :ref indicates that this is morphism between entities.
;;
;; - the permutation-json object [aql.brass.client/sample-submission-json]
;;
;;   * the target entities
;;   * the assignment of attributes to target entites
;;
;; - the source schema
;;
;;   * the source entities
;;   * the initial assignment of attributes to source entities
;;   *
;;

(def schema-permutation-mapping
  {"Source_Id" {:ent "source" :col "id"}
   "Source_Name" {:ent "source" :col "name"}
   "Source_Channel" {:ent "source" :col "channel"}

   "CotEvent_Id" {:ent "cot_event" :col "id"}
   "CotEvent_SourceId" {:ent "cot_event"
                        :col "source_id"
                        :ref "Source_Id"
                        :ref-name "has_source"}
   "CotEvent_CotType" {:ent "cot_event" :col "cot_type"}
   "CotEvent_How" {:ent "cot_event" :col "how"}
   "CotEvent_Detail" {:ent "cot_event" :col "detail"}
   "CotEvent_ServerTime" {:ent "cot_event" :col "servertime"}

   "Position_Id" {:ent "cot_event_position" :col "id"}
   "Position_EventId" {:ent "cot_event_position"
                       :col "cot_event_id"
                       :ref "CotEvent_Id"
                       :ref-name "has_cot_event"}
   "Position_PointHae" {:ent "cot_event_position" :col "point_hae"}
   "Position_PointCE" {:ent "cot_event_position" :col "point_ce"}
   "Position_PointLE" {:ent "cot_event_position" :col "point_le"}
   "Position_TileX" {:ent "cot_event_position" :col "tileX"}
   "Position_TileY" {:ent "cot_event_position" :col "tileY"}
   "Position_Longitude" {:ent "cot_event_position" :col "longitude"}
   "Position_Latitude" {:ent "cot_event_position" :col "latitude"}})

(s/def ::reference (s/tuple string? string? string?))
(s/def ::references (s/coll-of ::reference :kind vector? :distinct true))
(s/def ::column (s/coll-of string? :kind vector? :distinct true))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::aql/name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::schema-perturbation (s/keys :req [::tables ::references]))
