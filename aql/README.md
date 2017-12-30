# AQL Server

This server responds to AQL requests.

It is based on http://www.http-kit.org/

https://drive.google.com/open?id=1WWsJp0n2LscyhV_HdJF9wWfquaT1hkU74iiDeJLnl7M

## Files

### aql/README.md

You are looking at it.

### aql/deps.edn

Used by 'clj' and 'clojure' to initialize the environment.

### aql/src

The source code.

### aql/src/aql/scratch.clj

Contains my noodling about mosting interaction with the aql server.

### aql/src/aql/server.clj

The main chunk of code.
It provides an AQL server which may be called as part of the BRASS/DAS.

### aql/src/aql/demo.clj

A sample provided by Ryan Wisnesky to demonstrate some interesting points.

* query migration
* command execution

### aql/src/aql/brass/data.clj

The data used for the BRASS demonstration.

## Usage

Start the server.

clj -m aql.server

Run the client demo from the command line.

clj -m aql.brass.client-demo

This will submit a sample migration command from which the migrated queries will be extracted.


## References

https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk

<svn>/docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
 :: “Sample SubmissionModel value”

The schema definition is in:
<svn>/database/server/baseline_schema_ddl.sql

One suggestion was to wrap the command line jar as...
<svn>/das/das-service/src/main/java/mil/darpa/immortals/core/das/AdaptationManger.java
 :: performDFUDSLCheck()
