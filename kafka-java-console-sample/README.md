# IBM Message Hub Kafka Java console sample application
This Java console application demonstrates how to connect to [IBM Message Hub](https://console.ng.bluemix.net/docs/services/MessageHub/index.html), send and receive messages using the [Kafka](https://kafka.apache.org) Java API. It also shows how to create and list topics using the Message Hub Admin REST API.

It can be run locally on your machine or deployed into [IBM Cloud](https://console.ng.bluemix.net/).

For help with additional deployment modes, please refer to our [connection guide](https://console.bluemix.net/docs/services/MessageHub/messagehub127.html#connect_messagehub).

__Important Note__: This sample creates a topic with one partition on your behalf. On the Standard plan, this will incur a fee if the topic does not already exist.

## Running the application

The application can be run in the following environments:

* [IBM Cloud Kubernetes Service](./docs/Kubernetes_Service.md) 
* [IBM Cloud Foundry](./docs/Cloud_Foundry.md)
* [Docker Local](./docs/Docker_Local.md)
* [Local Developement](./docs/Local.md)

