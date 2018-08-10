# IBM Event Streams Kafka Node.js console sample application
This Node.js console application demonstrates how to connect to [IBM Event Streams](https://console.ng.bluemix.net/docs/services/MessageHub/index.html), send and receive messages using the [node-rdkafka](https://github.com/Blizzard/node-rdkafka) module. It also shows how to create and list topics using the Event Streams Admin REST API.

It can be run locally on your machine or deployed into [IBM Cloud®](https://console.ng.bluemix.net/).

In this tutorial, we'll focus on deploying the application into the IBM Cloud, therefore you don't have to build it locally, it will be done on your behalf on IBM Cloud.

__Important Note__: This sample creates on your behalf a topic named `kafka-nodejs-console-sample-topic` with one partition - this will incur a fee if the topic does not already exist on your account.

## Global Prerequisites
To run the sample, you must have done the following:
* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* [Event Streams Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) provisioned in [IBM Cloud®](https://console.ng.bluemix.net/)

## Prerequisites (IBM Cloud®)
* [Cloud Foundry Command Line Interface](https://github.com/cloudfoundry/cli/releases) installed

## Standard or Enterprise Plan?

**It's important to know which Event Streams plan you're using as the sample deployment steps are subtly different on each plan respectively.**

By this point, you should have an Event Streams instance provisioned. If you haven't done this step yet, please refer to the main [readme](/README.md).

If you are not sure what type of Event Streams instance you have then you can find this information out by visiting IBM Cloud's web console [dashboard](https://console.bluemix.net/dashboard).

*Please make sure you are in the appropriate Region, Account, Organization and Space where you provisioned your Event Streams instance!*

* Event Streams Standard plan services are "Cloud Foundry Services" with the plan column showing "Standard".
* Event Streams Enterprise plan services are "Services" with the plan column showing "Enterprise".


## Deploy the Application

As the Standard and Enterprise Plan deployment steps are subtly different, we split the deployment steps into separate sections. Please navigate to the appropriate page(s):

### [Standard Plan Deployment Guide](./standard_plan.md)

### [Enterprise plan Deployment Guide](./enterprise_plan.md)

### [Local Deployment](./local.md)
