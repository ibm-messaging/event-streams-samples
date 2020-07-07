
# Running in IBM Cloud Foundry

## Prerequisites
To build and run the sample, you must have the done the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Install the [IBM Cloud CLI](https://cloud.ibm.com/docs/cli?topic=cloud-cli-install-ibmcloud-cli)
* Provision an [Event Streams Service Instance](https://cloud.ibm.com/catalog/services/event-streams) in [IBM Cloud®](https://cloud.ibm.com/)
* Install [Gradle 4+](https://gradle.org/)
* Install Java 7+

## Standard/Enterprise Plan?

**It's important to know which Event Streams for IBM Cloud plan you're using as the sample deployment steps are subtly different on each plan respectively.**

By this point, you should have an Event Streams for IBM Cloud instance provisioned. If you haven't done this step yet, please refer to the main [readme](../README.md).

If you are not sure what type of Event Streams for IBM Cloud instance you have then you can find this information out by visiting IBM Cloud's web console [dashboard](https://cloud.ibm.com/resources).

*Please make sure you are in the appropriate Region, Account, Organization and Space where you provisioned your Event Streams instance!*

* Event Streams for IBM Cloud Standard plan services are "Services" with the plan column showing "Standard".
* Event Streams for IBM Cloud Enterprise plan services are "Services" with the plan column showing "Enterprise".


## Deploy the Application

The deployment for the Standard/Enterprise Plan can be found in the links listed below

### [Classic Plan Deployment Guide](CF_Classic_Plan.md)

### [Standard/Enterprise Plan Deployment Guide](CF_Standard_Enterprise_Plan.md)


## Further references

If you want find out more about Cloud Foundry applications then check the following documents:

[Cloud Foundry manifest documentation](http://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html)






