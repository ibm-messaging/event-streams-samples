/**
 * Copyright 2016 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corp. 2016
 */
package com.eventstreams.samples.servlet;

import com.eventstreams.samples.env.Environment;
import com.eventstreams.samples.env.EventStreamsCredentials;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@WebServlet("/KafkaServlet")
public class KafkaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(KafkaServlet.class);

    private final String serverConfigDir = System.getProperty("server.config.dir");
    private final String resourceDir = serverConfigDir + File.separator
            + "apps" + File.separator
            + "EventStreamsLibertyApp.war" + File.separator
            + "resources";
    private final String topic = "testTopic";

    private KafkaProducer<String, String> kafkaProducer;
    private String producedMessage;
    private ConsumerRunnable consumerRunnable;
    private Thread consumerThread = null;
    private int producedMessages = 0;
    private String currentConsumedMessage;
    private boolean messageProduced = false;

    /**
     * Intialising the KafkaServlet
     */
    public void init() {
        logger.log(Level.WARN, "Initialising Kafka Servlet");
        logger.log(Level.WARN, "Server Config directory: " + serverConfigDir);
        logger.log(Level.WARN, "Resource directory: " + resourceDir);

        // Retrieve credentials from environment
        EventStreamsCredentials credentials = Environment.getEventStreamsCredentials();
        StringBuilder boostrapServersBuilder = new StringBuilder();
        for (String broker : credentials.getKafkaBrokersSasl()) {
            boostrapServersBuilder.append(",");
            boostrapServersBuilder.append(broker);
        }
        String bootstrapServers = boostrapServersBuilder.toString();

        // Check topic
        RESTRequest restApi = new RESTRequest(credentials.getKafkaAdminUrl(), credentials.getApiKey());
        // Create a topic, ignore a 422 response - this means that the
        // topic name already exists.
        restApi.post("/admin/topics", "{ \"name\": \"" + topic + "\" }", new int[]{422});

        String topics = restApi.get("/admin/topics", false);
        logger.log(Level.WARN, "Topics: " + topics);

        // Initialize Kafka Producer
        kafkaProducer = new KafkaProducer(getClientConfiguration(bootstrapServers, credentials.getApiKey(), true));

        // Initialise Kafka Consumer
        consumerRunnable = new ConsumerRunnable(bootstrapServers, credentials.getApiKey(), topic);
        consumerThread = new Thread(consumerRunnable);
        consumerThread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the latest messages received from the Kafka Consumer.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().print("<h3>Already consumed messages: </h3>");
        for (String message : consumerRunnable.consumedMessages) {
            response.getWriter().print("<div class='message'><small class='code'>Message: " + message + "</small></div>");
        }
    }

    /**
     * Produces a message to an Event Streams endpoint.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Producing messages to a topic
        produce(topic);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Interrupted", e);
        }

        if (messageProduced) {
            response.getWriter()
                    .print("<div class='message'><a>Message produced: </a><small class='code'>" + producedMessage
                            + "</small> </div>,,");
            // Print out consumed messages
            response.getWriter()
                    .print("<div class ='message'><a>Message consumed : </a><small class='code'>" + currentConsumedMessage
                            + "</small></div>");
        }
    }

    /**
     * Retrieve client configuration information, using a properties file, for connecting to secure Kafka.
     *
     * @param broker
     *            {String} A string representing a list of brokers the producer can contact.
     * @param apikey
     *            {String} The API key of the IBM Event Streams service.
     * @param isProducer
     *            {Boolean} Flag used to determine whether or not the configuration is for a producer.
     * @return {Properties} A properties object which stores the client configuration info.
     */
    private Properties getClientConfiguration(String broker, String apikey, boolean isProducer) {
        Properties props = new Properties();
        InputStream propsStream;
        String fileName = resourceDir + File.separator;

        if (isProducer) {
            fileName += "producer.properties";
        } else {
            fileName += "consumer.properties";
        }

        try {
            logger.log(Level.WARN, "Reading properties file from: " + fileName);
            propsStream = new FileInputStream(fileName);
            props.load(propsStream);
            propsStream.close();
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
            return props;
        }

        props.put("bootstrap.servers", broker);

        //Adding in credentials for EventStreams auth
        String saslJaasConfig = props.getProperty("sasl.jaas.config");
        saslJaasConfig = saslJaasConfig.replace("APIKEY", apikey);
        props.setProperty("sasl.jaas.config", saslJaasConfig);

        logger.log(Level.WARN, "Using properties: " + props);

        return props;
    }

    /**
     * Produce a message to a <code>topic</code>
     *
     * @param topic
     */
    private void produce(String topic) {
        logger.log(Level.WARN, "Producer is starting.");

        String key = "key";
        // Push a message into the list to be sent.
        MessageList<String> list = new MessageList();
        producedMessage = "This is a test message, msgId=" + producedMessages;
        list.push(producedMessage);

        try {
            // Create a producer record which will be sent
            // to the Event Streams service, providing the topic
            // name, key and message.
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, list.build());

            // Synchronously wait for a response from Event Streams / Kafka.
            RecordMetadata m = kafkaProducer.send(record).get();
            producedMessages++;

            logger.log(Level.WARN, "Message produced, offset: " + m.offset());

            Thread.sleep(1000);
        } catch (final Exception e) {
            e.printStackTrace();
            // Consumer will hang forever, so exit program.
            System.exit(-1);
        }
        messageProduced = true;
        logger.log(Level.WARN, "Producer is shutting down.");
    }

    /**
     * Kafka consumer runnable which can be used to create and run consumer as a separate thread.
     */
    class ConsumerRunnable implements Runnable {
        private final KafkaConsumer<String, String> kafkaConsumer;
        private final List<String> consumedMessages = new ArrayList<>();

        private boolean closing = false;

        ConsumerRunnable(String broker, String apikey, String topic) {

            kafkaConsumer = new KafkaConsumer<>(getClientConfiguration(broker, apikey, false));
            kafkaConsumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {

                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                }

                @Override
                public void onPartitionsAssigned(Collection<org.apache.kafka.common.TopicPartition> partitions) {
                    try {
                        logger.log(Level.WARN, "Partitions " + partitions + " assigned, consumer seeking to end.");

                        for (TopicPartition partition : partitions) {
                            long position = kafkaConsumer.position(partition);
                            logger.log(Level.WARN, "current Position: " + position);

                            logger.log(Level.WARN, "Seeking to end...");
                            kafkaConsumer.seekToEnd(Arrays.asList(partition));
                            logger.log(Level.WARN,
                                    "Seek from the current position: " + kafkaConsumer.position(partition));
                            kafkaConsumer.seek(partition, position);
                        }
                        logger.log(Level.WARN, "Producer can now begin producing messages.");
                    } catch (final Exception e) {
                        logger.error("Error when assigning partitions", e);
                    }

                }
            });
        }

        @Override
        public void run() {
            logger.log(Level.WARN, "Consumer is starting.");

            while (!closing) {
                try {
                    currentConsumedMessage = "consumer is waiting for messages to be consumed ...";
                    Duration duration = Duration.of(1, ChronoUnit.SECONDS);
                    Iterator<ConsumerRecord<String, String>> it = kafkaConsumer.poll(duration).iterator();

                    // Iterate through all the messages received and print their content.
                    while (it.hasNext()) {
                        ConsumerRecord<String, String> record = it.next();
                        currentConsumedMessage = record.value() + ". Offset: " + record.offset();
                        consumedMessages.add(currentConsumedMessage);
                    }
                    kafkaConsumer.commitSync();

                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
                    logger.log(Level.ERROR, "Producer/Consumer loop has been unexpectedly interrupted");
                    shutdown();
                } catch (final Exception e) {
                    logger.log(Level.ERROR, "Consumer has failed with exception: " + e);
                    shutdown();
                }
            }

            logger.log(Level.WARN, "Consumer is shutting down.");
            kafkaConsumer.close();
            consumedMessages.clear();
        }

        public void shutdown() {
            closing = true;
        }
    }
}
