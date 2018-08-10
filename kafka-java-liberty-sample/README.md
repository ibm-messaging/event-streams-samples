# IBM Event Streams Kafka Liberty sample application

**Liberty for Java™ applications on IBM Cloud®** are powered by the IBM WebSphere® Liberty Buildpack. The Liberty profile is a highly composable, fast-to-start, dynamic application server runtime environment. It is part of IBM WebSphere Application Server v8.5.5.

This repository holds a sample cloud web application that was built using Liberty for Java™. The app will interact with a bound Event Streams service to produce and consume messages.

For more information regarding IBM Event Streams, [see the documentation on IBM Cloud®](https://www.ng.bluemix.net/docs/services/MessageHub/index.html).

__Important Note__: This sample creates a topic on your behalf with one partition - this will incur a fee if the topic does not already exist on your account.

## Prerequisites
To build and run the sample, you must have the done the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Install [Gradle](https://gradle.org/)
* Install Java 7+
* Install the [IBM Cloud CLI](https://console.bluemix.net/docs/cli/reference/bluemix_cli/download_cli.html)
* Provision a [Event Streams Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) in [IBM Cloud®](https://console.ng.bluemix.net/)

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
