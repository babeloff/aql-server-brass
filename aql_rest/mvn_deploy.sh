
target/aql-brass-server-2018.04.25-SNAPSHOT.jar
target/aql-brass-server-2018.04.25-SNAPSHOT-standalone.jar

VERSION="2018.04.18"

mvn deploy:deploy-file \
    -Durl=https://nexus.isis.vanderbilt.edu/repository/maven-snapshots \
    -DrepositoryId=snapshots \
    -Dfile=./target/aql-brass-server-${VERSION}-standalone.jar \
    -DgroupId=aql-brass-server \
    -DartifactId=aql-brass-server \
    -Dversion=${VERSION} \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DgeneratePom.description="The BRASS AQL server" \
    -DuniqueVersion=true
