# Running in IBM Cloud Kubernetes Service

## Prerequisites
To build and run the sample, you must have the done the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Install the [IBM Cloud CLI](https://cloud.ibm.com/docs/cli/reference/bluemix_cli?topic=cloud-cli-install-ibmcloud-cli)
* Install the [Kubernetes CLI](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* Provision an [Event Streams Service Enterprise Plan Instance](https://cloud.ibm.com/catalog/services/event-streams) in [IBM Cloud®](https://cloud.ibm.com/), this is the only plan to support the schema registry.
* Provision a [Kubernetes Service instance](https://cloud.ibm.com/kubernetes/catalog/cluster) in [IBM Cloud®](https://cloud.ibm.com/)


## Deploy the Application

1. From the Event Streams for IBM Cloud instance dashboard, click `Service Credentials` and select or create a new one. Copy its            content. 

2. To deploy the application you first need to bind the Event Streams for IBM Cloud service instance to the cluster. Replace `<Service Credentials>` with the content copied in step 1.
    ```shell
    kubectl create secret generic eventstreams-binding --from-literal=binding='<Service Credentials>'
    ```
    The command above creates a secret in your cluster named  `eventstreams-binding`. 

3. [Configure the CLI to run kubectl](https://cloud.ibm.com/docs/containers?topic=containers-cs_cli_install#cs_cli_configure)

4. Deploy the application in the cluster:
    ```shell
    kubectl apply -f kafka-java-console-schema-sample.yaml
    ```
5. Access the application logs:
    ```shell
    kubectl logs kafka-java-console-schema-sample --follow
    ```

## Further references

If you want find out more about IBM Cloud Kubernetes Service or Kubernetes then check the following documents:

[IBM Cloud Kubernetes Service](https://www.ibm.com/cloud/container-service)

[Kubernetes Documentation](https://kubernetes.io/docs/home/)


