#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# ----------------------------------------------------------------------------

set -e

# OS specific support.
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*)  msys=true   ;;
  Darwin*) darwin=true ;;
  NONSTOP*) nonstop=true ;;
esac

MAVEN_WRAPPER_JAR="${MAVEN_USER_HOME:-${HOME}/.m2}/wrapper/dists/mvn-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$(dirname "$0")/.mvn/wrapper/maven-wrapper.properties"

# Read properties
DISTRIBUTION_URL=$(grep "distributionUrl" "$MAVEN_WRAPPER_PROPERTIES" | sed 's/distributionUrl=//')
WRAPPER_URL=$(grep "wrapperUrl" "$MAVEN_WRAPPER_PROPERTIES" | sed 's/wrapperUrl=//')

MAVEN_HOME_PARENT="${MAVEN_USER_HOME:-${HOME}/.m2}/wrapper/dists"
MAVEN_HOME="$MAVEN_HOME_PARENT/$(basename "$DISTRIBUTION_URL" .zip)"

if [ ! -d "$MAVEN_HOME" ]; then
  echo "Downloading Apache Maven from $DISTRIBUTION_URL"
  mkdir -p "$MAVEN_HOME_PARENT"
  TEMP_ZIP="$MAVEN_HOME_PARENT/mvn-download-$$.zip"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$TEMP_ZIP" "$DISTRIBUTION_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$TEMP_ZIP" "$DISTRIBUTION_URL"
  else
    echo "ERROR: Neither curl nor wget is available. Cannot download Maven." >&2
    exit 1
  fi

  unzip -q "$TEMP_ZIP" -d "$MAVEN_HOME_PARENT"
  rm -f "$TEMP_ZIP"

  # The zip extracts to a subdirectory named after the artifact (e.g. apache-maven-3.9.9)
  EXTRACTED=$(find "$MAVEN_HOME_PARENT" -maxdepth 1 -name "apache-maven-*" -type d | head -1)
  if [ -n "$EXTRACTED" ] && [ "$EXTRACTED" != "$MAVEN_HOME" ]; then
    mv "$EXTRACTED" "$MAVEN_HOME"
  fi
fi

MVN_EXEC="$MAVEN_HOME/bin/mvn"

if [ ! -x "$MVN_EXEC" ]; then
  echo "ERROR: Could not find mvn executable at $MVN_EXEC" >&2
  exit 1
fi

exec "$MVN_EXEC" "$@"
