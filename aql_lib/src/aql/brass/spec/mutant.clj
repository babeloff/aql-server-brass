
(ns aql.brass.spec.mutant
  (:require [clojure.spec.alpha :as s]
            [aql.spec :as aql-spec]))

;; the name of a foreign-key reference
(s/def ::ref-name string?)
(s/def ::ent-name string?)
(s/def ::attr-name string?)
;; expresses the morphisms between entities
(s/def ::reference (s/tuple ::ref-name ::ent-name ::ent-name))
(s/def ::references (s/coll-of ::reference :kind vector? :distinct true))

;; ::origin indicates the entity for which is the parent of the attribute
(s/def ::origin ::ent-name)
;; ::coent indicates the cospan entity
(s/def ::coent ::ent-name)
;; ::coname is the cospan name for that attribute
;; also the original name if possible
(s/def ::coname ::attr-name)
;; ::numame is the new name if different from ::coname
(s/def ::nuname ::attr-name)
;; ::ref indicates that this is a morphism between entities.
;; it provides the key to that referenced column.
(s/def ::ref string?)
;; ::type is the datatype of the column
(s/def ::type #{"Integer" "Varchar" "Bigint" "Real"})
;; ::column is the column record, it carries enough information
;; for the target schema and mapping to the cospan.
(s/def ::column (s/keys :req [::origin ::coent ::coname ::type]
                        :opt [::nuname ::ref ::ref-name]))
(s/def ::columns (s/coll-of ::column :kind vector? :distinct true))
(s/def ::table (s/keys :req [::aql-spec/name ::columns]))
(s/def ::tables (s/coll-of ::table :kind vector? :distinct true))
(s/def ::mutant (s/keys :req [::tables ::references]))

;; This map contains information that could be
;; supplied in the permutation-json but is not.
;; It is implicit.

(def source
  {"table" "source"
   "columns"
   ["Source_Id"
    "Source_Name"
    "Source_Channel"]})

;; This map contains the information necessary to
;; construct the T schema and the G mapping.
;; It needs to be merged with the normalized mutant object.
;; The normalized mutant object is formed from
;; - [aql.brass.client/mutant-json]
;; - [aql.brass.spec.mutant/source]
;; by [aql.brass.mutant/normalize]

(def base-lookup
  {"Source_Id"
   {::origin "source"
    ::coent "source"
    ::coname "source_id"
    ::nuname "id"
    ::type "Varchar"}
   "Source_Name"
   {::origin "source"
    ::coent "source"
    ::coname "name"
    ::type "Varchar"}
   "Source_Channel"
   {::origin "source"
    ::coent "source"
    ::coname "channel"
    ::type "Varchar"}

   "Event_Id"
   {::origin "cot_event"
    ::coent "cospan"
    ::coname "cot_event_id"
    ::nuname "id"
    ::type "Varchar"}
   "Event_SourceId"
   {::origin "cot_event"
    ::coent "cospan"
    ::coname "source_id"
    ::ref "Source_Id"
    ::ref-name "source_fk"
    ::type "Varchar"}
   "Event_CotType"
   {::origin "cot_event"
    ::coent "cospan"
    ::coname "cot_type"
    ::type "Varchar"}
   "Event_How"
   {::origin "cot_event"
    ::coent "cospan"
    ::coname "how"
    ::type "Varchar"}
   "Event_Detail"
   {::origin "cot_event"
    ::coent "cospan"
    ::coname "detail"
    ::type "Varchar"}
   "Event_ServerTime"
   {::origin "cot_event"
    ::coent "cospan"
    ::coname "servertime"
    ::type "Varchar"}

   "Position_Id"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "cot_position_id"
    ::nuname "id"
    ::type "Varchar"}
   "Position_EventId"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "cot_event_id"
    ::ref "Event_Id"
    ::ref-name "cot_event_fk"
    ::type "Varchar"}
   "Position_PointHae"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "point_hae"
    ::type "Varchar"}
   "Position_PointCE"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "point_ce"
    ::type "Varchar"}
   "Position_PointLE"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "point_le"
    ::type "Varchar"}
   "Position_TileX"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "tilex"
    ::type "Varchar"}
   "Position_TileY"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "tiley"
    ::type "Varchar"}
   "Position_Longitude"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "longitude"
    ::type "Varchar"}
   "Position_Latitude"
   {::origin "cot_event_position"
    ::coent "cospan"
    ::coname "latitude"
    ::type "Varchar"}})

(def lookup
  (assoc base-lookup
         "CotEvent_Id" (get base-lookup "Event_Id")
         "CotEvent_SourceId" (get base-lookup "Event_SourceId")
         "source_id" (get base-lookup "Event_SourceId")
         "CotEvent_CotType" (get base-lookup "Event_CotType")
         "cot_type" (get base-lookup "Event_CotType")
         "CotEvent_How" (get base-lookup "Event_How")
         "how" (get base-lookup "Event_How")
         "CotEvent_Detail" (get base-lookup "Event_Detail")
         "detail" (get base-lookup "Event_Detail")
         "CotEvent_ServerTime" (get base-lookup "Event_ServerTime")
         "servertime" (get base-lookup "Event_ServerTime")
         "point_hae" (get base-lookup "Position_PointHae")
         "point_ce" (get base-lookup "Position_PointCE")
         "point_le" (get base-lookup "Position_PointLE")
         "tileX" (get base-lookup "Position_TileX")
         "tileY" (get base-lookup "Position_TileY")
         "longitude" (get base-lookup "Position_Longitude")
         "latitude" (get base-lookup "Position_Latitude")))
