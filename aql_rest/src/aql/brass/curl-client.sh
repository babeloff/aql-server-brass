#/bin/bash

# This does the same thing as client.clj but via curl
ENDPOINT="http://localhost:9090/brass/p2/c1/json"
HEADER="Content-Type: application/json; charset=utf8"

cat << JSON  | curl -d @- -H "${HEADER}"  ${ENDPOINT}
{"permutation":
   {"martiServerModel":
     {"requirements":
      {"postgresqlPerturbation":
       {"tables":
        [{"table":  "cot_action",
          "columns":
           ["Event_Id",
            "Event_SourceId",
            "Event_How",
            "Event_ServerTime",
            "Position_PointCE",
            "Position_PointLE",
            "Position_TileX",
            "Position_Longitude",
            "Position_Latitude"]},
         {"table": "cot_detail",
          "columns":
          ["Position_Id",
           "Position_EventId",
           "Position_PointHae",
           "Event_Detail",
           "Position_TileY",
           "Event_CotType"]}]}}}}}
JSON
