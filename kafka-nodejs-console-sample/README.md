# IBM Message Hub Kafka Node.js console sample application
This Node.js console application demonstrates how to connect to [IBM Message Hub](https://console.ng.bluemix.net/docs/services/MessageHub/index.html), send and receive messages using the [node-rdkafka](https://github.com/Blizzard/node-rdkafka) module. It also shows how to create and list topics using the Message Hub Admin REST API.

__Important Note__: This sample creates a topic with one partition on your behalf. On the Standard plan, this will incur a fee if the topic does not already exist.

## Running the application

The application can be run in the following environments:

* [IBM Cloud Kubernetes Service](./docs/Kubernetes_Service.md) 
* [IBM Cloud Foundry](./docs/Cloud_Foundry.md)
* [Docker Local](./docs/Docker_Local.md)
* [Local Development](./docs/Local.md)
