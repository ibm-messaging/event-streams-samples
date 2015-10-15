#!/bin/sh
#
# Copyright 2015 IBM
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#  limitations under the License.
#
#
# Licensed Materials - Property of IBM
# © Copyright IBM Corp. 2015
#
DIR=$(pwd)
GRADLE=$(which gradle)
TMP_DIR=/tmp/java-sample

if [ ! ${GRADLE} ]; then
  GRADLE=/opt/gradle/bin/gradle
fi;

echo 'pwd =' ${DIR}
echo 'temp =' ${TMP_DIR}
echo 'gradle =' ${GRADLE}

rm lib/*.jar
rm -d lib
mkdir lib
mkdir ${TMP_DIR}

# --- Download Kafka from GitHub ---
cd ${TMP_DIR}

git clone https://github.com/apache/kafka.git kafka
cd ${TMP_DIR}/kafka
git checkout f1110c3fbb166f94204b6bb18bc4e1a9100d3c4e

# Build and install Kafka 0.9.0.0
${GRADLE}
./gradlew clean && ./gradlew clients:build

# Copy Kafka Library
cd ${TMP_DIR}
cp ${TMP_DIR}/kafka/clients/build/libs/kafka-clients-0.9.0.0-SNAPSHOT.jar ${DIR}/lib

# --- Install log4g and slf4j ---
wget http://www.eu.apache.org/dist/logging/log4j/1.2.17/log4j-1.2.17.tar.gz
tar xzf log4j-1.2.17.tar.gz
cp apache-log4j-1.2.17/log4j-1.2.17.jar ${DIR}/lib

wget http://slf4j.org/dist/slf4j-1.7.6.tar.gz
tar xzf slf4j-1.7.6.tar.gz
cp slf4j-1.7.6/slf4j-api-1.7.6.jar ${DIR}/lib
cp slf4j-1.7.6/slf4j-log4j12-1.7.6.jar ${DIR}/lib

# --- Install Jackson ---
cd ${DIR}/lib
wget http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.5.4/jackson-core-2.5.4.jar
