# IBM Cloud Foundry deployment to an Enterprise Plan Event Streams

## Overview

To deploy and run the sample:
* Create a Cloud Foundry Service Alias for your Enterprise Service
* Setup your `manifest.yml` with your service details
* Use `ibmcloud cf push --no-start` to deploy the app to IBM Cloud Foundry
* Re-configure binding with Manager role
* Start the app
* Inspect the application's logs

## Set up a Cloud Foundry Service Alias
Before continuing, connect to IBM Cloud with the [IBM Cloud command line interface](https://console.bluemix.net/docs/cli/reference/bluemix_cli/get_started.html#getting-started).

The Enterprise plan is IAM enabled. Therefore the following extra step is required to create a Cloud Foundry alias for your Service:

Create a Cloud Foundry alias for your service's associated CRN:
```shell
ibmcloud resource service-alias-create <alias-name> --instance-name <eventstreams-service-name>
```

Having created this alias associated your Service with a Cloud Foundry Organization and Space, thereby enabling your Cloud Foundry application to referrence it and connect to it.

## Setup the manifest.yml

1. Select the Event Streams service you would like to bind your application to. Do this by replacing `<YOUR_SERVICE_INSTANCE_NAME>` with your service instance alias name in `manifest.yml`:
```yaml
  services:
    - "<YOUR_SERVICE_INSTANCE_NAME>"
```
2. Consider your domain: You might need to change this in the `manifest.yml` as the domain varies by IBM Cloud region. If unsure, just delete the domain line and IBM Cloud will pick the domain for you.

## Deploy the Application

Push the app without starting it immediately by running the following command in the same directory as the `manifest.yml` file:
```shell
ibmcloud app push --no-start
```

## Re-configure the binding
A binding between your app and service-alias is created for you automatically, but by default does not have permissions to create topics. This means that we need to delete the existing binding and create a new one with the correct role:

```
ibmcloud resource service-binding-delete <YOUR_SERVICE_INSTANCE_ALIAS_NAME> kafka-python-console-sample
ibmcloud resource service-binding-create <YOUR_SERVICE_INSTANCE_ALIAS_NAME> kafka-python-console-sample Manager
```

## Start the app
Now it should be safe to start the application:
```shell
ibmcloud app start kafka-python-console-sample
```

## Produce and Consume Messages
The sample application should have created the default sample topic and started producing and consuming messages in an infinite loop. View the logs to verify this:
```shell
ibmcloud app logs kafka-python-console-sample
```
