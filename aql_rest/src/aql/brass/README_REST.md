# BRASS AQL Server

This server responds to BRASS AQL permutation requests.


## Usage

There are several ways to use these functions.

### Server
Start the service.
The service can be run directly from the source via the clojure tooling.

```clj
clj -m aql.brass.server
```
...and with command line arguments.

```clj
clj -m aql.brass.server --port 23456
```


A precompiled [uber-jar is available here](https://nexus.isis.vanderbilt.edu/repository/maven-snapshots).
The easiest way to run the service is from the command line.
```bash
java -jar ./<where-ever>/aql-server-brass-<version>.jar
```

...and with command line arguments.

```bash
java -jar ./<where-ever>/aql-server-brass-<version>.jar -p 23456
```

In each case it writes a ready indicator to standard output.

```bash
STATE:[RUNNING]
```

It is also configured to run as a daemon proces.
Both Win32 and UNIX like platforms are supported.
For Win32 platforms use [procrun](http://commons.apache.org/proper/commons-daemon/procrun.html).
For UNIX like platforms use [jsvc](http://commons.apache.org/proper/commons-daemon/jsvc.html).

#### BRASS client https://github.com/babeloff/aql-server-brass/blob/master/aql/src/aql/brass/client.clj

Run the brass client demo from the command line.

```bash
clj -m aql.brass.client
```

This will submit a sample migration command from which the migrated queries will be extracted.

The content of the JSON passed should look something like this...

```json
{"martiServerModel"
   {"requirements"
    {"postgresqlPerturbation"
     {"tables"
      [{"table"  "cot_action"
        "columns"
         ["CotEvent_Id"
          "CotEvent_SourceId"
          "CotEvent_How"
          "CotEvent_ServerTime"
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
         "CotEvent_Detail"
         "Position_TileY"
         "CotEvent_CotType"]}]}}}})
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

### aql/src/aql/brass/server.clj

The main entry point for the service.
It provides an AQL server which may be called as part of the BRASS/DAS.

### aql/src/aql/brass/data.clj

The data used for the BRASS demonstration.

## References

https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk

<svn>/docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
 :: “Sample SubmissionModel value”

The schema definition is in:
<svn>/database/server/baseline_schema_ddl.sql

One suggestion was to wrap the command line jar as...
<svn>/das/das-service/src/main/java/mil/darpa/immortals/core/das/AdaptationManger.java
 :: performDFUDSLCheck()

Daemon http://commons.apache.org/proper/commons-daemon/index.html
