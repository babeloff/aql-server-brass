#/bin/bash

# This does the same thing as client.clj but via curl
ENDPOINT="http://localhost:9090/brass/p2/c1/json"
HEADER="Content-Type: application/json; charset=UTF-8"

cat << JR  | curl -d @- -H "${HEADER}"  ${ENDPOINT}  > ./retrieve.json
{"permutation":
   {"martiServerModel":
     {"requirements":
      {"postgresqlPerturbation":
       {"tables":
        [{"table":  "cot_action",
          "columns":
           ["CotEvent_How",
            "CotEvent_ServerTime",
            "Position_PointCE",
            "Position_PointLE",
            "Position_TileX",
            "Position_Longitude",
            "Position_Latitude"]},
         {"table": "cot_detail",
          "columns":
          ["Position_PointHae",
           "CotEvent_Detail",
           "Position_TileY",
           "CotEvent_CotType"]}]}}}}}
JR
