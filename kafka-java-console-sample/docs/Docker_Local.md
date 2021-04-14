
# Running in Docker Locally

## Prerequisites
To build and run the sample, you must have the done the following:

* Obtain this repository's contents, either use `git` or just download the samples as a ZIP
* Provision an [Event Streams Service Instance](https://cloud.ibm.com/catalog/services/event-streams) in [IBM Cloud®](https://cloud.ibm.com/)
* Install [Docker](https://docs.docker.com/install/)

## Run the Application

1. Build the container image from the `Dockerfile`:
    ```shell
    docker build -t java-console-sample .
    ```

2. Export the Event Streams for IBM Cloud instance credentials:

    From the Event Streams for IBM Cloud instance dashboard, click `Service Credentials` and select or create a new one. Copy its content and export it as below:
    ```shell
    export VCAP_SERVICES='{
        "instance_id": "...",
        "api_key": "...",
        "kafka_admin_url": "....",
        "kafka_rest_url": "...",
        "kafka_brokers_sasl": [
          ...
        ],
        "user": "...",
        "password": "..."
    }'
    ```

3. Run the container image
    ```shell
    docker run -e VCAP_SERVICES="$VCAP_SERVICES" java-console-sample
    ```

## Further references

If you want find out more about Docker then check the following document:

[Docker documentation](https://docs.docker.com/install/overview/)
