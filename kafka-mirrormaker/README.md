# kafka-mirrormaker

This repository contains the artifacts required to build the `event-streams-samples/kafka-mirrormaker` Docker image.

This image contains [Kafka Mirror Maker](http://kafka.apache.org/documentation/#basic_ops_mirror_maker) and can be used to replicate data between clusters.

A prebuilt image is provided on Github Packages, you can use the following command to pull the image:

```docker pull ghcr.io/ibm-messaging/event-streams-samples/kafka-mirrormaker:latest
```

## Running the image in Kubernetes

Instructions for running the `event-streams-samples/kafka-mirrormaker` image in Kubernetes can be found [here](IKS/README.md).

## Building the image

To build the image yourself, complete these steps:

1. Build the docker image:
    ```shell
    docker build .
    ```
If you want to use the sample [YAML file](IKS/kafka-mirrormaker.yaml), ensure that you update the image name with your own image name.
