# IBM Message Hub Kafka Java console sample application deployment to an Enterprise Plan Message Hub

## Overview

To run the samples, you will:

* Set up a Cloud Foundry Service Alias for your Service
* Setup your `manifest.yml` with your service details
* Build and package the sample application
* Use `ibmcloud cf push` to deploy the app to IBM Cloud®
* Create a binding in between your application and your Cloud Foundry Service Alias
* Start the application
* View the application's logs

## Prerequisites
* You logged in to IBM Cloud via the command line tool, [IBM Cloud CLI](https://console.bluemix.net/docs/cli/reference/bluemix_cli/get_started.html#getting-started)

## Set up a cloud Foundry alias for your Service
The Enterprise plan is IAM enabled. Therefore the following extra step is required to create a Cloud Foundry alias for your Service:

Create a Cloud Foundry alias for your service's associated CRN (your selected organization and space will be where your Service Alias will be created. If you wish to change your organization and space, you can use `ibmcloud target -cf`):

```shell
ibmcloud resource service-alias-create <messagehub-service-name> --instance-name <messagehub-service-name>
```

Having created this alias associated your Service with a Cloud Foundry Organization and Space, thereby enabling your Cloud Foundry application to referrence it and connect to it.

## Setup the manifest.yml
To deploy the sample as a Cloud Foundry application, you need to edit `manifest.yml`.

You need to point the application at your Message Hub Cloud Foundry Service **Alias** service by editing:

```
 services:
    - "<YOUR_MESSAGE_HUB_SERVICE_*ALIAS*_NAME>"
```

You can optionally rename your application, eg.:

```
name: kafka-java-console-sample
```

## Build the sample
Run the following commands on your local machine to build and package the sample application

```shell
gradle clean && gradle build
 ```

Ensure that the `gradle build` command has produced a zip file artifact under `build/distributions`.

Navigate to the appropriate Cloud Foundry Account, Organization and Space of your choice. If you're new to IBM Cloud then just navigate to the place where your Message Hub instance has been provisioned. *(If you're following this guide from the start then you can skip this step safely as you're already at the location where your Service Alias has been created.)*

## Push the application into IBM Cloud®
Push the app without starting it immediately by running the following command in the same directory as the `manifest.yml` file:
```
ibmcloud app push --no-start
```

## Re-configure the binding
A binding between your app and service-alias is created for you automatically, but by default does not have permissions to create topics. This means that we need to create the existing binding and create a new one with the correct role:

```
ibmcloud resource service-binding-delete <YOUR_SERVICE_INSTANCE_ALIAS_NAME> <YOUR APPLICATION'S NAME>
ibmcloud resource service-binding-create <YOUR_SERVICE_INSTANCE_ALIAS_NAME> <YOUR APPLICATION'S NAME> Manager
```

## Start the app

```shell
ibmcloud app start <your application's name>
```

The sample application should have created the default sample topic and started producing and consuming messages in an infinte loop. View the logs to verify this:

```
ibmcloud app logs <your app's name, eg. kafka-java-console-sample>
```

__Note:__ The IBM Cloud distribution will automatically update the required files for you at runtime,
using the `VCAP_SERVICES` information provided by IBM Cloud.

## Further references

If you are unfamiliar with Cloud Foundry applications then you might find the following document useful:

[Cloud Foundry manifest documentation](http://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html)