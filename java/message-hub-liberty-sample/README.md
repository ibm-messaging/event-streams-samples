# message-hub-liberty-sample
IBM® Bluemix® Message Hub is a scalable, distributed, high throughput messaging service built on the top of Apache Kafka. It underpins the integration of your on-premise and off-premise cloud services and technologies. You can wire micro-services together using open protocols, connect stream data to analytics to realise powerful insight and feed event data to multiple applications to react in real time.

**Liberty for Java™ applications on IBM® Bluemix®** are powered by the IBM WebSphere® Liberty Buildpack. The Liberty profile is a highly composable, fast-to-start, dynamic application server runtime environment. It is part of IBM WebSphere Application Server v8.5.5.

This repository holds a sample cloud web application that was built using Liberty for Java™. The app will interact with a bound Message Hub service to produce and consume messages.

For more information regarding IBM Message Hub, [see the documentation on Bluemix](https://www.ng.bluemix.net/docs/services/MessageHub/index.html).

__Important Note__: This sample creates a topic on your behalf with one partition - this will incur a fee if the topic does not already exist on your account.

## General Prerequisites
To build and run the sample, you must have the following installed:

* [git](https://git-scm.com/)
* [Gradle](https://gradle.org/)
* Java 7+
* [Message Hub Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) provisioned in [IBM Bluemix](https://console.ng.bluemix.net/)

If you are not familiar with Liberty for Java on Bluemix, please consult the [documentation](https://console.ng.bluemix.net/docs/starters/liberty/index.html#liberty).

## General Steps
To deploy and run the sample:
* Create a Message Hub service
* Add in JVM and LIBERTY licenses
* Run `gradle build war`
* Use `cf push` to deploy the app to Bluemix
* Open a browser and navigate to the app's url
* Press the button to produce message(s) to Kafka, you can then see the consumed messages.

## Building the Sample
Install the project using gradle:
```shell
gradle build war
 ```

You should see a directory called `target` created in your project home directory. A WAR file is created under `target/defaultServer`, as well as a copy of the server.xml file.

## Deployment Prerequisites
To deploy applications using the IBM WebSphere Application Server Liberty Buildpack, you are required to accept the IBM Liberty license and IBM JRE license by following the instructions below:

1. Read the current IBM [Liberty-License][] and the current IBM [JVM-License][].
2. Extract the `D/N: <License code>` from the Liberty-License and JVM-License.
3. Add the following environment variables and extracted license codes to the `manifest.yml` file in the directory from which you push your application. For further information on the format of
the `manifest.yml` file refer to the [manifest documentation][].

```yaml
env:
    IBM_JVM_LICENSE: <jvm license code>
    IBM_LIBERTY_LICENSE: <liberty license code>
```

__Note:__ Please use domain *eu-gb.mybluemix.net* within the manifest.yml if you are using Bluemix within London (console.eu-gb.bluemix.net). You may also need to use a unique hostname e.g. *host: JohnsSampleLibertyApp*

## Deploy the Sample to Bluemix
Now we can push the app to Bluemix:
```shell
cf push
 ```

The `cf push` command deploys the sample into Bluemix with a random route and binds the Message Hub service. You can find out the hostname of the deployed app from the output of this command. For example:
```shell
requested state: started
instances: 1/1
usage: 256M x 1 instances
urls: messagehublibertyapp.mybluemix.net
last uploaded: Mon Feb 29 15:44:29 UTC 2016
stack: cflinuxfs2
buildpack: Liberty for Java(TM) (SVR-DIR, liberty-2016.2.0_0, buildpack-v2.5-20160209-1336, ibmjdk-1.8.0_20160108, env)
 ```

## Produce and Consume Messages
Once the sample has been successfully deployed, navigate to the URL stated in the deployment logs. Once here, you can produce a message by clicking on the `Post Message` button.

If the message was successfully produced and then consumed, you will then see the prompted message:

##### Already consumed messages:
```shell
Message: [{"value":"This is a test message, msgId=0"}]. Offset: 1
```

##### We have produced a message: ```This is a test message, msgId=0```
```shell
Consumed messages: [{"value":"This is a test message, msgId=0"}]. Offset: 1
```
 
[Liberty-License]: http://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/8.5.5.7/lafiles/runtime/en.html
[JVM-License]: http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?la_formnum=&li_formnum=L-JWOD-9SYNCP&title=IBM%C2%AE+SDK%2C+Java+Technology+Edition%2C+Version+8.0&l=en
[manifest documentation]: http://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html
