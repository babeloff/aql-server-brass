
(ns aql.brass.spec.mutant
  (:require [clojure.spec.alpha :as s]
            [aql.spec :as aql-spec]))

;; ::entity indicates the entity for which is the parent of the attribute
;; ::cospan is the original name for that attribute
;; ::type is the datatype of the column
;; ::ref indicates that this is morphism between entities.

(s/def ::reference (s/tuple string? string? string?))
(s/def ::references (s/coll-of ::reference :kind vector? :distinct true))

(s/def ::entity string?)
(s/def ::cospan string?)
(s/def ::prime string?)
(s/def ::ref string?)
(s/def ::ref-name string?)
(s/def ::type #{"Integer" "Varchar" "Bigint"})
(s/def ::column (s/keys :req [::entity ::cospan ::type]
                        :opt [::prime ::ref ::ref-name]))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::aql-spec/name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::mutant (s/keys :req [::tables ::references]))

;; This map contains information that should be
;; supplied in the permutation-json but is not.

(def source
  {"table" "source"
   "columns"
   ["Source_Id"
    "Source_Name"
    "Source_Channel"]})

;; This map contains the information necessary to construct the
;; prime T schema and the G mapping.
;; It needs to be merged with the normalized mutant object.
;; The normalized mutant object is formed from
;; - [aql.brass.client/mutant-json]
;; - [aql.brass.spec.mutant/source]
;; by [aql.brass.mutant/normalize]

(def base-lookup
  {"Source_Id"
   {::entity "source"
    ::cospan "source_id"
    ::prime "id"
    ::type "Integer"}
   "Source_Name"
   {::entity "source"
    ::cospan "name"
    ::type "Varchar"}
   "Source_Channel"
   {::entity "source"
    ::cospan "channel"
    ::type "Varchar"}

   "Event_Id"
   {::entity "cot_event"
    ::cospan "cot_event_id"
    ::prime "id"
    ::type "Integer"}
   "Event_SourceId"
   {::entity "cot_event"
    ::cospan "source_id"
    ::ref "Source_Id"
    ::ref-name "has_source"
    ::type "Integer"}
   "Event_CotType"
   {::entity "cot_event"
    ::cospan "cot_type"
    ::type "Varchar"}
   "Event_How"
   {::entity "cot_event"
    ::cospan "how"
    ::type "Varchar"}
   "Event_Detail"
   {::entity "cot_event"
    ::cospan "detail"
    ::type "Text"}
   "Event_ServerTime"
   {::entity "cot_event"
    ::cospan "servertime"
    ::type "Bigint"}

   "Position_Id"
   {::entity "cot_event_position"
    ::cospan "cot_position_id"
    ::prime "id"
    ::type "Integer"}
   "Position_EventId"
   {::entity "cot_event_position"
    ::cospan "cot_event_id"
    ::ref "Event_Id"
    ::ref-name "has_cot_event"
    ::type "Integer"}
   "Position_PointHae"
   {::entity "cot_event_position"
    ::cospan "point_hae"
    ::type "Integer"}
   "Position_PointCE"
   {::entity "cot_event_position"
    ::cospan "point_ce"
    ::type "Integer"}
   "Position_PointLE"
   {::entity "cot_event_position"
    ::cospan "point_le"
    ::type "Integer"}
   "Position_TileX"
   {::entity "cot_event_position"
    ::cospan "tileX"
    ::type "Integer"}
   "Position_TileY"
   {::entity "cot_event_position"
    ::cospan "tileY"
    ::type "Integer"}
   "Position_Longitude"
   {::entity "cot_event_position"
    ::cospan "longitude"
    ::type "Real"}
   "Position_Latitude"
   {::entity "cot_event_position"
    ::cospan "latitude"
    ::type "Real"}})

(def lookup
  (assoc base-lookup
         "CotEvent_Id" (get base-lookup "Event_Id")
         "CotEvent_SourceId" (get base-lookup "Event_SourceId")
         "CotEvent_CotType" (get base-lookup "Event_CotType")
         "CotEvent_How" (get base-lookup "Event_How")
         "CotEvent_Detail" (get base-lookup "Event_Detail")
         "CotEvent_ServerTime" (get base-lookup "Event_ServerTime")))
