# kafka-connect

This repository contains the artifacts required to build the `ibmcom/eventstreams-kafkaconnect` Docker image.

This image contains the Kafka Connect runtime and the [IBM Cloud Object Storage sink connector](https://github.com/ibm-messaging/kafka-connect-ibmcos-sink) and the [IBM MQ source connector](https://github.com/ibm-messaging/kafka-connect-mq-source).

A prebuilt image is provided on DockerHub: https://hub.docker.com/r/ibmcom/eventstreams-kafkaconnect.

## Running the image in Kubernetes

Instructions for running the `eventstreams-kafkaconnect` image in Kubernetes can be found [here](IKS/README.md).

## Building the image

In case you don't want to use the image we provide, you can build an image by completing these steps:

1. Run the `build.sh` script to download and compile the connectors:
    ```shell
    ./build.sh
    ```

2. Build the docker image:
    ```shell
    docker build .
    ```
If you want to use the sample [YAML file](IKS/kafka-connect.yaml), be sure to update the image name with your own image name.
