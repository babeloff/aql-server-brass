# BRASS AQL Library

This library is intended for use with AQL BRASS servers.

## Usage

This will submit a sample migration command from which the migrated queries will be extracted.

The content of the JSON passed should look something like this...

```json
{"martiServerModel"
   {"requirements"
    {"postgresqlPerturbation"
     {"tables"
      [{"table"  "cot_action"
        "columns"
         ["Event_Id"
          "Event_SourceId"
          "Event_How"
          "Event_ServerTime"
          "Position_PointCE"
          "Position_PointLE"
          "Position_TileX"
          "Position_Longitude"
          "Position_Latitude"]}
       {"table" "cot_detail"
        "columns"
        ["Position_Id"
         "Position_EventId"
         "Position_PointHae"
         "Event_Detail"
         "Position_TileY"
         "Event_CotType"]}]}}}})
```

## Files

### aql/README.md

You are looking at it.

### aql/deps.edn

Used by 'clj' and 'clojure' to initialize the environment.

### aql/src

The source code.

### aql/scratch/

Contains my noodling about mosting interaction with the aql server.

### aql/src/aql/brass/cospan.clj

The main factory for the service.

### aql/src/aql/brass/data.clj

The data used for the BRASS demonstration.

## References

https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk

<svn>/docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
 :: “Sample SubmissionModel value”

The schema definition is in:
<svn>/database/server/baseline_schema_ddl.sql
