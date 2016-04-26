IBM® Bluemix® Message Hub is a scalable, distributed, high throughput messaging service built on the top of Apache Kafka. It underpins the integration of your on-premise and off-premise cloud services and technologies. You can wire micro-services together using open protocols, connect stream data to analytics to realise powerful insight and feed event data to multiple applications to react in real time.

**Liberty for Java™ applications on IBM® Bluemix®** are powered by the IBM WebSphere® Liberty Buildpack. The Liberty profile is a highly composable, fast-to-start, dynamic application server runtime environment. It is part of IBM WebSphere Application Server v8.5.5.

This repository holds a sample cloud web application that was built using Liberty for Java™. The app will interact with a bound message hub service to produce and consume messages.

For more information regarding IBM Message Hub, [see the documentation on Bluemix](https://www.ng.bluemix.net/docs/services/MessageHub/index.html).

*Important Note: The samples in this repository will create topic(s) on your behalf - creating a topic incurs a fee. For more information, view the README files in each part of the repository and consult the Bluemix documentation if necessary.*

##General Prerequisites

To build and run the sample, you must have the following installed:

* [git](https://git-scm.com/)
* [Gradle](https://gradle.org/)
* Java 7+
* [Message Hub Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) provisioned in [IBM Bluemix](https://console.ng.bluemix.net/)

We assume the reader has relevant knowledge of above technologies. In addition, if you are not familiar with Liberty for Java on Bluemix, please consult the [documentation](https://console.ng.bluemix.net/docs/starters/liberty/index.html#liberty).

#General steps

To deploy and run the sample app:
* Create a Message Hub service
* Pull the Liberty sample from GitHub
* Add in JVM and LIBERTY licenses
* Run a Gradle build and war
* Use `cf push` to deploy the app to Bluemix e.g. `cf push MessageHubLibertyApp -p target/defaultServer`
* Open a browser and navigate to the app's url.
* Press the button to produce message(s) to Kafka, you can then see the consumed messages.

##Pull the sample project

You can clone and pull the project from the public git repository to your local work space.

##Setup and install the project

Once you have the project pulled to your local workspace, you need to update the server.xml file.

The default location for messagehub-login.jar on Bluemix is:
```shell
${server.config.dir}/apps/MessageHubLibertyApp.war/WEB-INF/lib
 ```


The `#USERNAME` and `#PASSWORD` fields are automatically updated by the sample app, if you keep the placeholders as they are. The app will retrieve the username and passwords from the Message Hub VCAP services.  

Install the project using gradle:
```shell
gradle build war
 ```

You should see a directory called `target` created in your project home directory. A WAR file is created under `target/defaultServer`, as well as a copy of the server.xml file.

## Usage
To deploy applications using the IBM WebSphere Application Server Liberty Buildpack, you are required to accept the IBM Liberty license and IBM JRE license by following the instructions below:

1. Read the current IBM [Liberty-License][] and the current IBM [JVM-License][].
2. Extract the `D/N: <License code>` from the Liberty-License and JVM-License.
3. Add the following environment variables and extracted license codes to the `manifest.yml` file in the directory from which you push your application. For further information on the format of
the `manifest.yml` file refer to the [manifest documentation][].

    ```
      env:
        IBM_JVM_LICENSE: <jvm license code>
        IBM_LIBERTY_LICENSE: <liberty license code>
    ```


**Note:** Please use domain *eu-gb.mybluemix.net* within the manifest.yml if you are using Bluemix within London (console.eu-gb.bluemix.net). You may also need to use a unique hostname e.g. *host: JohnsSampleLibertyApp*


##Deploy the sample app to Bluemix

Now we can push the app to Bluemix:
```shell
cd ${LIBERTY_PROJECT_DIR}
cf push MessageHubLibertyApp -p target/defaultServer -m 256M
 ```

From the `cf push` log you can find out the hostname of the deployed app. For example:
```shell
requested state: started
instances: 1/1
usage: 256M x 1 instances
urls: messagehublibertyapp.mybluemix.net
last uploaded: Mon Feb 29 15:44:29 UTC 2016
stack: cflinuxfs2
buildpack: Liberty for Java(TM) (SVR-DIR, liberty-2016.2.0_0, buildpack-v2.5-20160209-1336, ibmjdk-1.8.0_20160108, env)
 ```

##Bind message hub to the liberty sample

Again, we use `cf` to bind the Message Hub service to the Liberty app:
```shell
cf bind-service $LIBERTY_APP_NAME $BLUEMIX_SERVICE_NAME
 ```
Then, we restage the Liberty app:
```shell
cf restage $LIBERTY_APP_NAME
 ```
Now your app should be live. You can access it using the given hostname, e.g.:
```shell
messagehublibertyapp.mybluemix.net
 ```

##Produce and consume messages

On the liberty app web page, you can produce a message by click on the button `post message`.

If the message was successfully produced and then consumed, then you can see the prompted message:

#####Already consumed messages:
```shell
Message: [{"value":"This is a test message, msgId=0"}]. Offset: 1
 ```

#####We have produced a message: ```shell This is a test message, msgId=0  ```
```shell
Consumed messages: [{"value":"This is a test message, msgId=0"}]. Offset: 1
 ```
 
[Liberty-License]: http://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/8.5.5.7/lafiles/runtime/en.html
[JVM-License]: http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?la_formnum=&li_formnum=L-JWOD-9SYNCP&title=IBM%C2%AE+SDK%2C+Java+Technology+Edition%2C+Version+8.0&l=en
[manifest documentation]: http://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html
