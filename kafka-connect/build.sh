#!/bin/sh
# Builds the Connectors for the Dockerfile requires Maven, git and gradle 
set -e

# Cleanup
rm -rf ./connectors/*
rm -rf ./kafka-connect-ibmcos-sink
rm -rf ./kafka-connect-mq-source
mkdir -p ./connectors

# Build COS Sink
(
git clone https://github.com/ibm-messaging/kafka-connect-ibmcos-sink
cd kafka-connect-ibmcos-sink
gradle clean shadowJar
cp ./build/libs/kafka-connect-ibmcos-sink-*-all.jar ../connectors
)

# Build MQ Source
(
git clone https://github.com/ibm-messaging/kafka-connect-mq-source
cd kafka-connect-mq-source
mvn clean package
cp ./target/kafka-connect-mq-source-*-jar-with-dependencies.jar ../connectors
)
