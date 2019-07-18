
## Deploying `eventstreams-kafkaconnect` to Kubernetes connecting to IBM Event Streams

### Prerequisites

- `kubectl` access to a Kubernetes cluster.
- Credentials for an IBM Event Streams instance that has the following permissions:
    - to create topics required by the Kafka Connect configuration (see `connect-distributed.properties`)
    - to read/write to the topics accessed by the Connectors 

### Configure Kafka Connect

Edit `connect-distributed.properties` replacing the `<BOOTSTRAP_SERVERS>` and `<APIKEY>` placeholders with your Event Streams credentials.

Create the following Kubernetes resources:

```shell
kubectl create secret generic connect-distributed-config --from-file=connect-distributed.properties
kubectl create configmap connect-log4j-config --from-file=connect-log4j.properties
```

### Run Kafka Connect in distributed mode in your Kubernetes cluster

Deploy the `ibmcom/eventstreams-kafkaconnect` Docker image:

```shell
kubectl apply -f ./kafka-connect.yaml
```
Note that the sample yaml file specifies a single replica. Edit the `replicas` field if you want to run multiple Connect workers.
Also, note that affinity rules might be needed to spread the workers across nodes.  

### Manage Connectors

To manage connectors, port forward to the `kafkaconnect-service` Service on port 8083:

```shell
kubectl port-forward service/kafkaconnect-service 8083
```

The Connect REST API is then available via `http://localhost:8083`.
The Connect REST API is documented at https://kafka.apache.org/documentation/#connect_rest

### Run Connectors

When the Kafka Connect runtime is running, see the instructions for running the connectors:
- [Run the COS Sink connector](https://github.com/ibm-messaging/kafka-connect-ibmcos-sink#running-the-connector)
- [Run the MQ Source connector](https://github.com/ibm-messaging/kafka-connect-mq-source#running-the-connector)
