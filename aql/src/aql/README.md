# AQL Server

This server responds to AQL requests.

It is based on http://www.http-kit.org/

https://drive.google.com/open?id=1WWsJp0n2LscyhV_HdJF9wWfquaT1hkU74iiDeJLnl7M



## References

https://dsl-external.bbn.com/tracsvr/immortals/browser/trunk

<svn>/docs/CP/Immortals-Phase2-cp1-SchemaMigration.md
 :: “Sample SubmissionModel value”

The schema definition is in:
<svn>/database/server/baseline_schema_ddl.sql

One suggestion was to wrap the command line jar as...
<svn>/das/das-service/src/main/java/mil/darpa/immortals/core/das/AdaptationManger.java
 :: performDFUDSLCheck()
