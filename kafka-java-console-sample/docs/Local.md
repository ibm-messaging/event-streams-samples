# IBM Event Streams for IBM Cloud Kafka Java console sample application: Local Development guide
As pushing the application into IBM Cloud® does not require you to build the application locally, this guide is here to guide you through the process, should you wish to build the application locally.

We will not discuss establishing a connection from your laptop to Event Streams for IBM Cloud. This is described in the [connection guide](https://cloud.ibm.com/docs/services/EventStreams?topic=eventstreams-connecting#connecting).

## Prerequisites
* Provision an [Event Streams Service Instance](https://cloud.ibm.com/catalog/services/event-streams) in [IBM Cloud®](https://cloud.ibm.com/)
* Install [Gradle 4+](https://gradle.org/)
* Install Java 7+

## Build the Sample
Build the project using gradle:
```shell
gradle clean build
 ```

The command above creates a jar file under `build/libs`.

## Running the Sample
Once built, to run the sample, execute the following command:
```shell
java -jar ./build/libs/kafka-java-console-sample-2.0.jar <kafka_brokers_sasl> <api_key>
```

To find the values for `<kafka_brokers_sasl>` and `<api_key>`, access your Event Streams instance in IBM Cloud®, go to the `Service Credentials` tab and select the `Credentials` you want to use.  If your user value is `token`, specify that with the password seperated by a `:`.

__Note__: `<kafka_brokers_sasl>` must be a single string enclosed in quotes. For example: `"host1:port1,host2:port2"`. We recommend using all the Kafka hosts listed in the `Credentials` you selected.

Alternatively, you can run only the producer or only the consumer by respectively appending the switches `-producer` or `-consumer`  to the command above.

The sample will run indefinitely until interrupted. To stop the process, use `Ctrl+C`.
