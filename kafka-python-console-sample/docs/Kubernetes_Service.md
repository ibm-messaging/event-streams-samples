# Running in IBM Cloud Kubernetes Service

## Prerequisites
To build and run the sample, you must have the done the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Install the [IBM Cloud CLI](https://console.bluemix.net/docs/cli/reference/bluemix_cli/download_cli.html)
* Install the [Kubernetes CLI](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* Provision a [Event Streams Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) in [IBM Cloud®](https://console.ng.bluemix.net/)
* Provision a [Kubernetes Service instance](https://console.bluemix.net/containers-kubernetes/catalog/cluster) in [IBM Cloud®](https://console.ng.bluemix.net/)


## Deploy the Application

1. From the Event Streams instance dashboard, click `Service Credentials` and select or create a new one. Copy its content. 

2. To deploy the application you first need to bind the Event Streams service instance to the cluster. Replace `<Service Credentials>` with the content copied in step 1.
    ```shell
    kubectl create secret generic eventstreams-binding --from-literal=binding='<Service Credentials>'
    ```
    The command above creates a secret in your cluster named `eventstreams-binding`. 

3. [Configure the CLI to run kubectl](https://console.bluemix.net/docs/containers/cs_cli_install.html#cs_cli_configure)

4. Deploy the application in the cluster:
    ```shell
    kubectl apply -f kafka-python-console-sample.yaml
    ```
5. Access the application logs:
    ```shell
    kubectl logs kafka-python-console-sample --follow
    ```

## Further references

If you want find out more about IBM Cloud Kubernetes Service or Kubernetes then check the following documents:

[IBM Cloud Kubernetes Service](https://www.ibm.com/cloud/container-service)

[Kubernetes Documentation](https://kubernetes.io/docs/home/)


