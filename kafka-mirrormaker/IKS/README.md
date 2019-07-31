
## Deploying `eventstreams-kafkamirrormaker` to Kubernetes to replicate data between 2 Event Streams clusters

These steps detail how to replicate data from a Kafka cluster (source) to another Kafka cluster (destination) using the `eventstreams-kafkamirrormaker` image.

### Prerequisites

- `kubectl` access to a Kubernetes cluster.
- Credentials for an IBM Event Streams instance that has the following permissions:
    - to read/write to the topics

Mirror Maker does not automatically create topics in the destination cluster. You must create these topics before starting Mirror Maker.

### Configure Mirror Maker

Edit `source.properties` replacing the `<BOOTSTRAP_SERVERS>` and `<APIKEY>` placeholders with your Event Streams credentials for the source cluster.

Edit `destination.properties` replacing the `<BOOTSTRAP_SERVERS>` and `<APIKEY>` placeholders with your Event Streams credentials for the destination cluster.

Create the following Kubernetes resources:

```shell
kubectl create secret generic source-config --from-file=source.properties
kubectl create secret generic destination-config --from-file=destination.properties
kubectl create configmap tools-log4j-config --from-file=tools-log4j.properties
```

### Run Mirror Maker in your Kubernetes cluster

Deploy the `ibmcom/eventstreams-kafkamirrormaker` Docker image:

```shell
kubectl apply -f ./kafka-mirrormaker.yaml
```
