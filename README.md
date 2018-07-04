# message-hub-samples
IBM Message Hub is a scalable, distributed, high throughput message bus to unite your on-premise and off-premise cloud technologies. You can wire micro-services together using open protocols, connect stream data to analytics to realise powerful insight and feed event data to multiple applications to react in real time.

This repository is for samples which interact with the Message Hub service. 
Currently, there are samples for the Kafka and MQ Light APIs.
Information and instructions regarding the use of these samples can be found in their respective directories.

## Aim of the Samples
The aim of the samples is to help you get started with Message Hub within minutes. They are not production-ready applications but should give you useful pointers at how to build, package and deploy applications as well as how to make basic API calls against us with error handling. We did all the heavy lifting so you can focus on developing exciting code with value!

## Provisioning your Message Hub Cluster
In order to provision a Message Hub cluster, please visit the [IBM Cloud® catalog](https://console.stage1.bluemix.net/catalog/). Please also familiarise yourself with Message Hub and Apache Kafka basics and terminology. [Our documentation](https://console.bluemix.net/docs/services/MessageHub/) is a good starting point.


### Pricing plans
IBM Message Hub can be provisioned on IBM Cloud® in various pricing plans. Please refer to our [documentation](https://console.bluemix.net/docs/services/MessageHub/messagehub085.html#plan_choose) to help choose a plan that works for you.

__Important Note__: Provisioning a Message Hub service in IBM Cloud® incurs a fee. Please review pricing before provisioning. The samples in this repository will create topic(s) on your behalf - creating a topic might also incur a fee. For more information, please consult the IBM Cloud® documentation if necessary.


## Connecting to your Message Hub Cluster
In each sample, we demonstrate a single connection path for both our Standard and Enterprise plans respectively. The aim was to get you started quickly. However your client's needs might be different. Therefore we wrote a [guide](https://console.bluemix.net/docs/services/MessageHub/messagehub127.html#connect_messagehub) that discusses credential generation in detail and showing you all possible ways of doing this.

## Our APIs and Sample Applications

### Kafka API (recommended):
* [kafka-java-console-sample](/kafka-java-console-sample/README.md) : Sample Java console application using the Message Hub Kafka API
* [kafka-java-liberty-sample](/kafka-java-liberty-sample/README.md) : Sample IBM Websphere Liberty profile application using the Message Hub Kafka API
* [kafka-nodejs-console-sample](kafka-nodejs-console-sample/README.md) : Sample Node.js console application using the Message Hub Kafka API
* [kafka-python-console-sample](/kafka-python-console-sample/README.md) : Sample Python console application using the Message Hub Kafka API

### MQ Light API:
* [mqlight](/mqlight/README.md) : MQ Light samples in Java, Python, Node.js and Ruby

## Get Further Assistance

If you have any issues, just ask us a question (tagged with message-hub) on [StackOverflow.com](http://stackoverflow.com/questions/tagged/message-hub).


For more information regarding IBM Message Hub, [view the documentation on IBM Cloud](https://www.console.ng.bluemix.net/docs/services/MessageHub/index.html).