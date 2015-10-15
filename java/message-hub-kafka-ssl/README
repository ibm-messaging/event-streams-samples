### message-hub-kafka-ssl
This Java sample can connect to one of the Message Hub Kafka endpoints. After it
has connected, it will produce and consume three messages before exiting.

## Prerequisites
To build and run the sample, you must have the following installed:
* [git](https://git-scm.com/)
* [Gradle](https://gradle.org/)
* Java 7+

## Building the Sample
Firstly, make sure you have the required libraries installed. If you are running a *nix system,
you can ```cd``` to the message-hub-kafka-ssl directory and run the following command:

```shell
./install-deps.sh
```

If you are running on a Windows Operating System,
[follow the linked instructions](https://www.ng.bluemix.net/docs/services/MessageHub/index.html).
Make sure all the libraries are placed in a folder called ```lib```.

Next, you will need to update the consumer and producer properties files. These can be found
in the ```resources``` directory. The only keys you need to update in each of the consumer and
producer properties files are the following:

* ssl.truststore.location
  * Set to the location of the `cacerts` file, most commonly located in _java.home_/lib/security
* ssl.truststore.password
  * If you have not changed the password, the default is _changeit_

After installing the required dependencies and updating the properties files, run the following commands:
```shell
gradle clean && gradle build
 ```

Once built, the sample can be located in the ```build/libs``` directory, along with the
```resources``` folder.

## Running the Sample
To run the sample, navigate to the ```build/libs``` directory and run the following command:
```shell
java -jar <name_of_jar>.jar <kafka_endpoint> <rest_endpoint> <api_key>
```

## Sample Output
```
class com.example.ConsumerRunnable is starting.
class com.example.ProducerRunnable is starting.
Message produced, offset: 0
Message produced, offset: 1
Message produced, offset: 2
class com.example.ProducerRunnable is shutting down.
Message: [{"value":"This is a test message0"}]
Message: [{"value":"This is a test message1"}]
Message: [{"value":"This is a test message2"}]
class com.example.ConsumerRunnable is shutting down.
```
