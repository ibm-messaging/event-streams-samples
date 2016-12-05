# message-hub-samples
IBM Message Hub is a scalable, distributed, high throughput message bus to unite your on-premise and off-premise cloud technologies. You can wire micro-services together using open protocols, connect stream data to analytics to realise powerful insight and feed event data to multiple applications to react in real time.

This repository is for samples which interact with the Message Hub service. 
Currently, there are samples for the Kafka, MQ Light and REST APIs.
Information and instructions regarding the use of these samples can be found in their respective directories.

### Kafka API (recommended):
* kafka-java-console-sample : Sample Java console application using the Message Hub Kafka API
* kafka-java-liberty-sample : Sample IBM Websphere Liberty profile application using the Message Hub Kafka API
* kafka-nodejs-console-sample : Sample Node.js console application using the Message Hub Kafka API

### MQ Light API:
* mqlight : MQ Light samples in Java, Python, Node.js and Ruby

### REST API:
* rest-nodejs-express-sample : Sample Node.js/Express application using the Message Hub REST API

If you have any issues, just ask us a question (tagged with message-hub) on [StackOverflow.com](http://stackoverflow.com/questions/tagged/message-hub).


For more information regarding IBM Message Hub, [view the documentation on Bluemix](https://www.ng.bluemix.net/docs/services/MessageHub/index.html).

__Important Note__: The samples in this repository will create topic(s) on your behalf - creating a topic incurs a fee. For more information, view the README files in each part of the
repository and consult the Bluemix documentation if necessary.
