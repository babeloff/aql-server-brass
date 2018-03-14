
(ns aql.brass.spec.mutant
  (:require [clojure.spec.alpha :as s]
            [aql.spec :as aql]))

;; This map contains the information necessary to construct the
;; target schema and the G mapping.
;;
;; The other relevant information is obtained by merging
;; - this map [aql.brass.spec/lookup]
;;
;; ::entity indicates the entity for which is the parent of the attribute
;; ::cospan is the original name for that attribute
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
  {"Source_Id" {::entity "source" ::cospan "source_id" ::target "id"}
   "Source_Name" {::entity "source" ::cospan "name"}
   "Source_Channel" {::entity "source" ::cospan "channel"}

   "Event_Id" {::entity "cot_event" ::cospan "cot_event_id" ::target "id"}
   "Event_SourceId" {::entity "cot_event"
                     ::cospan "source_id"
                     :ref "Source_Id"
                     :ref-name "has_source"}
   "Event_CotType" {::entity "cot_event" ::cospan "cot_type"}
   "Event_How" {::entity "cot_event" ::cospan "how"}
   "Event_Detail" {::entity "cot_event" ::cospan "detail"}
   "Event_ServerTime" {::entity "cot_event" ::cospan "servertime"}

   "Position_Id" {::entity "cot_event_position" ::cospan "cot_position_id" ::target "id"}
   "Position_EventId" {::entity "cot_event_position"
                       ::cospan "cot_event_id"
                       :ref "Event_Id"
                       :ref-name "has_cot_event"}
   "Position_PointHae" {::entity "cot_event_position" ::cospan "point_hae"}
   "Position_PointCE" {::entity "cot_event_position" ::cospan "point_ce"}
   "Position_PointLE" {::entity "cot_event_position" ::cospan "point_le"}
   "Position_TileX" {::entity "cot_event_position" ::cospan "tileX"}
   "Position_TileY" {::entity "cot_event_position" ::cospan "tileY"}
   "Position_Longitude" {::entity "cot_event_position" ::cospan "longitude"}
   "Position_Latitude" {::entity "cot_event_position" ::cospan "latitude"}})

(s/def ::reference (s/tuple string? string? string?))
(s/def ::references (s/coll-of ::reference :kind vector? :distinct true))
(s/def ::column (s/coll-of string? :kind vector? :distinct true))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::aql/name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::schema-mutation (s/keys :req [::tables ::references]))
