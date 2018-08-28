# IBM Cloud Foundry deployment to a Standard Plan Message Hub

## Overview

To deploy and run the sample:
* Setup your `manifest.yml` with your service details
* Use `ibmcloud cf push` to deploy the app to IBM Cloud Foundry
* Use `ibmcloud cf logs` to check the application.

## Setup the manifest.yml

1. Select the Message Hub service you would like to bind your application to. Do this by replacing `<YOUR_SERVICE_INSTANCE_NAME>` with your actual service's name in `manifest.yml`:
```yaml
  services:
    - "<YOUR_SERVICE_INSTANCE_NAME>"
```
2. Consider your domain: You might need to change this in the `manifest.yml` as the domain varies by IBM Cloud region. If unsure, just delete the domain line and IBM Cloud will pick the domain for you.


## Deploy the Sample to IBM Cloud Foundry
Before continuing, connect to IBM Cloud with the [IBM Cloud command line interface](https://console.bluemix.net/docs/cli/reference/bluemix_cli/get_started.html#getting-started).

Once connected to IBM Cloud, push the app:
```shell
ibmcloud app push
```

## Produce and Consume Messages
The sample application should have created the default sample topic and started producing and consuming messages in an infinite loop. View the logs to verify this:
```shell
ibmcloud app logs kafka-python-console-sample
```