
# Running in IBM Cloud Foundry

## Prerequisites
To build and run the sample, you must have the done the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Install the [IBM Cloud CLI](https://console.bluemix.net/docs/cli/reference/bluemix_cli/download_cli.html)
* Provision a [Event Streams Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) in [IBM Cloud®](https://console.ng.bluemix.net/)

## Standard or Enterprise Plan?

**It's important to know which Event Streams plan you're using as the sample deployment steps are subtly different on each plan respectively.**

By this point, you should have an Event Streams instance provisioned. If you haven't done this step yet, please refer to the main [readme](../README.md).

If you are not sure what type of Event Streams instance you have then you can find this information out by visiting IBM Cloud's web console [dashboard](https://console.bluemix.net/dashboard).

*Please make sure you are in the appropriate Region, Account, Organization and Space where you provisioned your Event Streams instance!*

* Event Streams Standard plan services are "Cloud Foundry Services" with the plan column showing "Standard".
* Event Streams Enterprise plan services are "Services" with the plan column showing "Enterprise".


## Deploy the Application

As the Standard and Enterprise Plan deployment steps are subtly different, we split the deployment steps into separate sections. Please navigate to the appropriate page(s):

### [Standard Plan Deployment Guide](CF_Standard_Plan.md)

### [Enterprise Plan Deployment Guide](CF_Enterprise_Plan.md)


## Further references

If you want find out more about Cloud Foundry applications then check the following documents:

[Cloud Foundry manifest documentation](http://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html)






