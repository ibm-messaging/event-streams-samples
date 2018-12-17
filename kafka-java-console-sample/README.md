# IBM Event Streams for IBM Cloud Kafka Java console sample application
This Java console application demonstrates how to connect to [IBM Event Streams for IBM Cloud](https://console.ng.bluemix.net/docs/services/EventStreams/index.html), send and receive messages using the [Kafka](https://kafka.apache.org) Java API. It also shows how to create topics using the Kafka Admin API.

It can be run locally on your machine or deployed into [IBM Cloud](https://console.ng.bluemix.net/).

For help with additional deployment modes, please refer to our [connection guide](https://console.bluemix.net/docs/services/EventStreams/eventstreams127.html#connecting).

__Important Note__: This sample creates a topic with one partition on your behalf. On the Standard plan, this will incur a fee if the topic does not already exist.

## Running the application

The application can be run in the following environments:

* [IBM Cloud Kubernetes Service](./docs/Kubernetes_Service.md) 
* [IBM Cloud Foundry](./docs/Cloud_Foundry.md)
* [Docker Local](./docs/Docker_Local.md)
* [Local Development](./docs/Local.md)

