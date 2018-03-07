
(ns aql.brass.spec.mutant
  (:require [clojure.spec.alpha :as s]
            [aql.spec :as aql]))

;; This map contains the information necessary to construct the
;; target schema and the G mapping.
;;
;; The other relevant information is obtained by merging
;; - this map [aql.brass.spec/lookup]
;;
;; :ent indicates the entity for which is the parent of the attribute
;; :cscol is the original name for that attribute
;; :ref indicates that this is morphism between entities.
;;
;; - the permutation-json object [aql.brass.client/mutant-json]
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

(def lookup
  {"Source_Id" {:ent "source" :cscol "source_id" :tgcol "id"}
   "Source_Name" {:ent "source" :cscol "name"}
   "Source_Channel" {:ent "source" :cscol "channel"}

   "Event_Id" {:ent "cot_event" :cscol "cot_event_id" :tgcol "id"}
   "Event_SourceId" {:ent "cot_event"
                     :cscol "source_id"
                     :ref "Source_Id"
                     :ref-name "has_source"}
   "Event_CotType" {:ent "cot_event" :cscol "cot_type"}
   "Event_How" {:ent "cot_event" :cscol "how"}
   "Event_Detail" {:ent "cot_event" :cscol "detail"}
   "Event_ServerTime" {:ent "cot_event" :cscol "servertime"}

   "Position_Id" {:ent "cot_event_position" :cscol "cot_position_id" :tgcol "id"}
   "Position_EventId" {:ent "cot_event_position"
                       :cscol "cot_event_id"
                       :ref "Event_Id"
                       :ref-name "has_cot_event"}
   "Position_PointHae" {:ent "cot_event_position" :cscol "point_hae"}
   "Position_PointCE" {:ent "cot_event_position" :cscol "point_ce"}
   "Position_PointLE" {:ent "cot_event_position" :cscol "point_le"}
   "Position_TileX" {:ent "cot_event_position" :cscol "tileX"}
   "Position_TileY" {:ent "cot_event_position" :cscol "tileY"}
   "Position_Longitude" {:ent "cot_event_position" :cscol "longitude"}
   "Position_Latitude" {:ent "cot_event_position" :cscol "latitude"}})

(s/def ::reference (s/tuple string? string? string?))
(s/def ::references (s/coll-of ::reference :kind vector? :distinct true))
(s/def ::column (s/coll-of string? :kind vector? :distinct true))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::aql/name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::schema-mutation (s/keys :req [::tables ::references]))
