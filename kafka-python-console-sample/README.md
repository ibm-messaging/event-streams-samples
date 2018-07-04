# IBM Message Hub Kafka Python console sample application
This Python console application demonstrates how to connect to [IBM Message Hub](https://console.ng.bluemix.net/docs/services/MessageHub/index.html), send and receive messages using the [confluent-kafka-python](https://github.com/confluentinc/confluent-kafka-python) library. It also shows how to create and list topics using the Message Hub Admin REST API.

This tutorial will explain how to run the sample app in [IBM Cloud®](https://console.ng.bluemix.net/) using a Message Hub Service. It is also possible to host the application locally and still target your Message Hub Service running in IBM Cloud but that is not covered during this tutorial.

__Important Note__: This sample creates on your behalf a topic named `kafka-python-console-sample-topic` with one partition - this will incur a fee if the topic does not already exist on your account.

## Prerequisites
To build and run the sample, you must have the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Install the [IBM Cloud CLI](https://console.bluemix.net/docs/cli/reference/bluemix_cli/download_cli.html)
* Provision a [Message Hub Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) in [IBM Cloud®](https://console.ng.bluemix.net/)

## Standard or Enterprise Plan?

**It's important to know which Message Hub plan you're using as the sample deployment steps are subtly different on each plan respectively.**

By this point, you should have a Message Hub instance provisioned. If you haven't done this step yet, please refer to the main [readme](/README.md).

If you are not sure what type of Message Hub instance you have then you can find this information out by visiting IBM Cloud's web console [dashboard](https://console.bluemix.net/dashboard).

*Please make sure you are in the appropriate Region, Account, Organization and Space where you provisioned your Message Hub instance!*

* Message Hub Standard plan services are "Cloud Foundry Services" with the plan column showing "Standard".
* Message Hub Enterprise plan services are "Services" with the plan column showing "Enterprise".


## Deploy the Application

As the Standard and Enterprise Plan deployment steps are subtly different, we split the deployment steps into separate sections. Please navigate to the appropriate page(s):

### [Standard Plan Deployment Guide](./standard_plan.md#running-the-sample-ibm-cloud)

### [Enterprise plan Deployment Guide](./enterprise_plan.md#running-the-sample-ibm-cloud)