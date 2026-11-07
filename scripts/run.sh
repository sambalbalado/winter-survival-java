#!/usr/bin/env sh
set -eu
cd "$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
# The runtime libraries remain separate so their original notices stay with them.
mvn -q package org.apache.maven.plugins:maven-dependency-plugin:3.7.1:copy-dependencies -DincludeScope=runtime
exec java -jar target/winter-survival-java-1.0.0.jar "$@"
