# IBM Message Hub Kafka Java console sample application deployment to a Standard Plan Message Hub


## Overview
To run the samples, you will:
* Setup your `manifest.yml` with your service details
* Build and package the sample application
* Use `ibmcloud cf push` to deploy the app to IBM Cloud®
* View the application's logs

## Setup the manifest.yml
To deploy the sample as a Cloud Foundry application, you need to edit `manifest.yml`.

You need to point the application at your Message Hub service by editing:

```
 services:
    - "<YOUR_MESSAGE_HUB_SERVICE_NAME>"
```

You can optionally rename your application, eg.:

```
name: kafka-java-console-sample
```

## Running the Build Script
Run the following commands on your local machine, after the prerequisites for your environment have been completed:
```shell
gradle clean && gradle build
 ```

## Running the Sample on IBM Cloud
1) Ensure that the previous `gradle build` command has produced a zip file artifact under `build/distributions`.

2) Before continuing, connect to IBM Cloud with the [IBM Cloud command line interface](https://console.bluemix.net/docs/cli/reference/bluemix_cli/get_started.html#getting-started).

3) Navigate to the appropriate Cloud Foundry Account, Organization and Space of your choice. If you're new to IBM Cloud then just navigate to the place where your Message Hub instance has been provisioned.

4) Push and start the application in IBM Cloud:

```
ibmcloud app push
```

5) The sample application should have created the default sample topic and started producing and consuming messages in an infinte loop. View the logs to verify this:

```
ibmcloud app logs <your app's name, eg. kafka-java-console-sample>
```

__Note:__ The IBM Cloud distribution will automatically update the required files for you at runtime,
using the `VCAP_SERVICES` information provided by IBM Cloud.

## Further references

If you are unfamiliar with Cloud Foundry applications then you might find the following document useful:

[Cloud Foundry manifest documentation](http://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html)