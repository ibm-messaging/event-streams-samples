# IBM Cloud Foundry deployment to an Standard/Enterprise Plan Event Streams for IBM Cloud

## Overview

To deploy and run the sample:
* Create a Cloud Foundry Service Alias for your Standard/Enterprise Service
* Setup your `manifest.yml` with your service details
* Use `ibmcloud cf push --no-start` to deploy the app to IBM Cloud Foundry
* Re-configure binding with Manager role
* Start the app
* Inspect the application's logs

## Set up a Cloud Foundry Service Alias
Before continuing, connect to IBM Cloud with the [IBM Cloud command line interface](https://cloud.ibm.com/docs/cli?topic=cloud-cli-ibmcloud-cli).

The Standard/Enterprise plan is IAM enabled. Therefore the following extra step is required to create a Cloud Foundry alias for your Service:

Create a Cloud Foundry alias for your service's associated CRN:
```shell
ibmcloud resource service-alias-create <eventstreams-service-alias-name> --instance-name <eventstreams-service-instance-name>
```

Having created this alias associated your Service with a Cloud Foundry Organization and Space, thereby enabling your Cloud Foundry application to referrence it and connect to it.

## Setup the manifest.yml
To deploy applications using the IBM WebSphere Application Server Liberty Buildpack, you are required to accept the IBM Liberty license and IBM JRE license by following the instructions below:

1. Read the current IBM [Liberty-License](http://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/8.5.5.7/lafiles/runtime/en.html) and the current IBM [JVM-License](http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?la_formnum=&li_formnum=L-JWOD-9SYNCP&title=IBM%C2%AE+SDK%2C+Java+Technology+Edition%2C+Version+8.0&l=en).
2. Select the Event Streams for IBM Cloud service you would like to bind your application to. Do this by replacing `<YOUR_SERVICE_INSTANCE_NAME>` with your service instance alias name in `manifest.yml`:
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

You should see a directory called `target` created in your project home directory. A WAR file is created under `target/defaultServer`, as well as a copy of the server.xml file.

## Deploy the Application

Push the app without starting it immediately by running the following command in the same directory as the `manifest.yml` file:
```shell
ibmcloud app push --no-start
```

## Re-configure the binding
A binding between your app and service-alias is created for you automatically, but by default does not have permissions to create topics. This means that we need to delete the existing binding and create a new one with the correct role:

```
ibmcloud resource service-binding-delete <YOUR_SERVICE_INSTANCE_ALIAS_NAME> <YOUR APPLICATION'S NAME>
ibmcloud resource service-binding-create <YOUR_SERVICE_INSTANCE_ALIAS_NAME> <YOUR APPLICATION'S NAME> Manager
```

## Start the app
Now it should be safe to start the application, **make sure you capture this output as it display's your application's URL binding**:
```shell
ibmcloud app start <YOUR APPLICATION'S NAME>
```
You can optionally inspect the app's logs (The app only logs when the UI button gets hit):
```shell
ibmcloud app logs <YOUR APPLICATION'S NAME>
```

## Produce and Consume Messages
Once the sample has been successfully deployed, navigate to the URL **stated in the start logs of your `ibmcloud app start` command above**. This url is made up from the app name and domain that were specified in the manifest.yml. Once here, you can produce a message by clicking on the `Post Message` button.

If the message was successfully produced and then consumed, you will then see the prompted message:

##### Already consumed messages:
```shell
Message: [{"value":"This is a test message, msgId=0"}]. Offset: 1
```

##### We have produced a message: ```This is a test message, msgId=0```
```shell
Consumed messages: [{"value":"This is a test message, msgId=0"}]. Offset: 1
```
