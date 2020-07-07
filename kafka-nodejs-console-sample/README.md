# IBM Event Streams for IBM Cloud Kafka Node.js console sample application
This Node.js console application demonstrates how to connect to [IBM Event Streams for IBM Cloud](https://cloud.ibm.com/docs/services/EventStreams?topic=eventstreams-getting_started), send and receive messages using the [node-rdkafka](https://github.com/Blizzard/node-rdkafka) module. It also shows how to create topics using the Kafka Admin API as exposed by `node-rdkafka`.

__Important Note__: This sample creates a topic with one partition on your behalf. On the Standard plan, this will incur a fee if the topic does not already exist.

## Running the application

The application can be run in the following environments:

* [IBM Cloud Kubernetes Service](./docs/Kubernetes_Service.md) 
* [IBM Cloud Foundry](./docs/Cloud_Foundry.md)
* [Docker Local](./docs/Docker_Local.md)
* [Local Development](./docs/Local.md)
