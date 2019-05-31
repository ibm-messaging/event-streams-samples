# IBM Cloud Foundry deployment to a Classic Plan Event Streams for IBM Cloud

## Overview

To deploy and run the sample:
* Setup your `manifest.yml` with your service details and licenses
* Build your app with `gradle build war`
* Use `ibmcloud cf push` to deploy the app to IBM Cloud Foundry
* Open a browser and navigate to the app's url
* Press the button to produce message(s) to Kafka, you can then see the consumed messages.

## Setup the manifest.yml
To deploy applications using the IBM WebSphere Application Server Liberty Buildpack, you are required to accept the IBM Liberty license and IBM JRE license by following the instructions below:

1. Read the current IBM [Liberty-License](http://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/8.5.5.7/lafiles/runtime/en.html) and the current IBM [JVM-License](http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?la_formnum=&li_formnum=L-JWOD-9SYNCP&title=IBM%C2%AE+SDK%2C+Java+Technology+Edition%2C+Version+8.0&l=en).
2. Select the Event Streams for IBM Cloud service you would like to bind your application to. Do this by replacing `<YOUR_SERVICE_INSTANCE_NAME>` with your actual service's name in `manifest.yml`:
```yaml
  services:
    - "<YOUR_SERVICE_INSTANCE_NAME>"
```
3. Consider your domain: You might need to change this in the `manifest.yml` as the domain varies by IBM Cloud region. If unsure, just delete the domain line and IBM Cloud will pick the domain for you.
4. Extract the `D/N: <License code>` from the Liberty-License and JVM-License.
5. Add the following environment variables and extracted license codes to the `manifest.yml` file in the directory from which you push your application. For further information on the format of
the `manifest.yml` file refer to the [manifest documentation].

```yaml
env:
    IBM_JVM_LICENSE: <jvm license code>
    IBM_LIBERTY_LICENSE: <liberty license code>
```

__Note:__ You may need to use a unique hostname e.g. *host: JohnsSampleLibertyApp*

## Build the Sample
Build the project using gradle:
```shell
gradle build war
 ```

You should see a directory called `target` created in your project home directory. A WAR file is created under `target/defaultServer`, as well as a copy of the `server.xml` file.

## Deploy the Sample to IBM Cloud Foundry
Before continuing, connect to IBM Cloud with the [IBM Cloud command line interface](https://cloud.ibm.com/docs/cli?topic=cloud-cli-ibmcloud-cli).


Once connected to IBM Cloud, push the app, **make sure you capture this output as it display's your application's URL binding**:

```shell
ibmcloud app push
```

## Produce and Consume Messages
Once the sample has been successfully deployed, navigate to the URL **stated in the start logs of your `ibmcloud app push` command above**. This url is made up from the app name and domain that were specified in the manifest.yml. Once here, you can produce a message by clicking on the `Post Message` button.

If the message was successfully produced and then consumed, you will then see the prompted message:

##### Already consumed messages:
```shell
Message: [{"value":"This is a test message, msgId=0"}]. Offset: 1
```

##### We have produced a message: ```This is a test message, msgId=0```
```shell
Consumed messages: [{"value":"This is a test message, msgId=0"}]. Offset: 1
```
