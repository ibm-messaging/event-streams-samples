# IBM Message Hub Liberty sample application

This repository holds a sample application that was built using Liberty for Java™. The application will interact with a Message Hub service to produce and consume messages.

**Liberty for Java™ applications on IBM Cloud®** are powered by the IBM WebSphere® Liberty Buildpack. The Liberty profile is a highly composable, fast-to-start, dynamic application server runtime environment. It is part of IBM WebSphere Application Server v8.5.5.

For more information regarding IBM Message Hub, [see the documentation on IBM Cloud®](https://www.ng.bluemix.net/docs/services/MessageHub/index.html).

__Important Note__: This sample creates a topic with one partition on your behalf. On the Standard plan, this will incur a fee if the topic does not already exist.

## Running the application

The application can be run in the following environments:

* [IBM Cloud Kubernetes Service](./docs/Kubernetes_Service.md) 
* [IBM Cloud Foundry](./docs/Cloud_Foundry.md)
* [Docker Local](./docs/Docker_Local.md)
