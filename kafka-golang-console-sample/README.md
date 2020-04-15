# IBM Event Streams for IBM Cloud Kafka Golang sample application: Local Development guide

As pushing the application into IBM Cloud® does not require you to build the application locally, this guide is here to guide you through the process, should you wish to build the application locally.

We will not discuss establishing a connection from your laptop to Event Streams for IBM Cloud. This is described in the [connection guide](https://cloud.ibm.com/docs/services/EventStreams?topic=eventstreams-connecting#connecting).

## Prerequisites

1. **If you don't already have one, create an Event Streams service instance.**

   1. Log in to the IBM Cloud console.
  
   2. Click **Catalog**.
  
    3. From the navigation pane, click **Integration**, click the **Event Streams** tile, and then select the **Lite plan**. The Event Streams service instance page opens.
  
   4. Enter a name for your service. You can use the default value.
  
   5. Click **Create**. The Event Streams **Getting started** page opens. 

2. **If you don't already have them, install the following prerequisites:**
	
	* [git](https://git-scm.com/)
	* [Go](https://golang.org/doc/install)


## Steps to Build the Sample

### 1. **Create a topic**

   The topic is the core of Event Streams flows. Data passes through a topic from producing applications to consuming applications. 

   We'll be using the IBM Cloud console (UI) to create the topic, and will reference it when starting the application.

   1. Go to the **Topics** tab.
  
   2. Click **New topic**.
  
   3. Name your topic.
  
        > The sample application is configured to connect to topic `kafka-golang-sample-topic`. If the topic does not exist, it is created when the application is started. 

   4. Keep the defaults set in the rest of the topic creation, click **Next** and then **Create topic**.

   5. The topic appears in the table. Congratulations, you have created a topic!

---

### 2. **Create credentials**

   To allow the sample application to access your topic, we need to create some credentials for it. 

   1. Go to **Service credentials** in the navigation pane.
  
   2. Click **New credential**.
  
   3. Give the credential a name so you can identify its purpose later. You can accept the default value.
  
   4. Give the credential the **Manager** role so that it can access the topics, and create them if necessary. 
  
   5. Click **Add**. The new credential is listed in the table in **Service credentials**.
  
   6. Click **View credentials** to see the `api_key` and `kafka_brokers_sasl` values.

---

### 3. **Clone the Github repository for the sample application**

   The sample application is stored here. Clone the `event-streams-samples` repository by running the clone command from the command line. 

   ```
    git clone https://github.com/ibm-messaging/event-streams-samples.git
   ```

   <br/>
   When the repository is cloned, from the command line change into the <code>kafka-golang-sample</code> directory.

   ```
   cd event-streams-samples/kafka-golang-sample
   ```

   <br/>
   Downloading and install the packages the contents of the <code>kafka-golang-sample</code> directory.

   ```
   go build
   ```
---

### 4.  **Run the consuming application**
   
   Edit the file `parameters.sh`, replacing the `kafka_brokers_sasl`, `api_key`, and any other values you may wish to change
   
   Use the `kafka_brokers_sasl` from the **Service credentials** created in Step 2. We recommend using all the `kafka_brokers_sasl` listed in the **Service credentials** that you created.

   >The `kafka_brokers_sasl` must be formatted as `"host:port,host2:port2"`. </br> Format the contents of `kafka_brokers_sasl` in a text editor before entering it in the command line.

   Then, use the `api_key` from the **Service credentials** created in Step 2. 

   You can set `topic-name` if you have a specific topic to consume from. Otherwise, the application would default to a topic called `kafka-golang-sample-topic`.

   Change `TO_RUN` to `consumer` to run the consumer rather than the producer.

   ```
   source ./parameters.sh
   ./golang-sample
   ```

   An `Waiting for messages to consume...` is displayed when the consuming application is running, but there is no data being consumed. 

---

### 5. **Run the producing application**

   Open a new command line window and change into the <code>kafka-golang-sample</code> directory.

   ```
   cd event-streams-samples/kafka-golang-sample
   ```
   
   Then, update the `parameters.sh` file and start the sample producing application from the command line, keeping the `KAFKA_ENDPOINTS` and `API_KEY` the same ones used to run the consumer.

   The optional `MESSAGE_COUNT` parameter can be added to decide the number of messages to produce to your topic.

   If you set a specific `TOPIC_NAME` for the consumer, make sure you use the same one with the producer. 

   Change `TO_RUN` to `producer` to run the producer rather than the consumer.

   ```
   source ./parameters.sh
   ./golang-sample
   ```

---

### 6. **Success!**

   When the producer starts, messages are produced to the topic. Messages are then consumed from the topic by the consuming application.
   You can verify the successful flow of messages when you see `Consumed message...` from the consumer. 

   The sample runs indefinitely until you stop it. To stop the process, run an exit command `Ctrl+C`.