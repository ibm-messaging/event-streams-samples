/**
 * Copyright 2015 IBM
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
 * (c) Copyright IBM Corp. 2015
 */
package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Sample used for interacting with Message Hub over Secure Kafka / Kafka Native
 * channels.
 * 
 * @author IBM
 */
public class KafkaNativeSample {
    public static final int MAX_PASSED_MESSAGES = 3;

    public static void main(String args[]) throws InterruptedException,
            ExecutionException, IOException {
        if (args.length != 3) {
            System.err
                    .println("Usage: <file_name>.jar <kafka_endpoint> <rest_endpoint> <api_key>");
            return;
        }

        String kafkaHost = args[0];
        String restHost = args[1];
        String apiKey = args[2];
        String topic = "mytopic";
        Thread consumerThread, producerThread;
        RESTRequest restApi = new RESTRequest(restHost, apiKey);

        System.out.println("Kafka Endpoint: " + kafkaHost);
        System.out.println("Rest API Endpoint: " + restHost);

        // Create a topic, ignore a 422 response - this means that the
        // topic name already exists.
        restApi.post("/admin/topics", "{ \"name\": \"" + topic + "\" }",
                new int[] { 422 });

        String topics = restApi.get("/admin/topics", false);

        System.out.println("Topics: " + topics);

        producerThread = createMessageProducer(kafkaHost, apiKey, topic);
        consumerThread = createMessageConsumer(kafkaHost, apiKey, topic);

        // Start producer and consumer threads.
        if (consumerThread != null) {
            consumerThread.start();
        } else {
            System.err
                    .println("Consumer thread is null. Make sure all provided details are valid.");
        }

        if (producerThread != null) {
            producerThread.start();
        } else {
            System.err
                    .println("Producer thread is null. Make sure all provided details are valid.");
        }
    }

    /**
     * Create a message consumer, returning the thread object it will run on.
     * 
     * @param broker
     *            {String} The host and port of the broker to interact with.
     * @param apiKey
     *            {String} The API key used to connect to the Message Hub
     *            service.
     * @param topic
     *            {String} The topic to consume on.
     * @return {Thread} Thread object that the consumer will run on.
     */
    public static Thread createMessageConsumer(String broker, String apiKey,
            String topic) {
        ConsumerRunnable consumerRunnable = new ConsumerRunnable(broker,
                apiKey, topic);
        return new Thread(consumerRunnable);
    }

    /**
     * Create a message producer, returning the thread object it will run on.
     * 
     * @param broker
     *            {String} The host and port of the broker to interact with.
     * @param apiKey
     *            {String} The API key used to connect to the Message Hub
     *            service.
     * @param topic
     *            {String} The topic to consume on.
     * @param message
     *            {String} String representation of a JSON array of messages to
     *            send.
     * @return {Thread} Thread object that the producer will run on.
     */
    public static Thread createMessageProducer(String broker, String apiKey,
            String topic) {
        ProducerRunnable producerRunnable = new ProducerRunnable(broker,
                apiKey, topic);
        return new Thread(producerRunnable);
    }

    /**
     * Retrieve client configuration information, using a properties file, for
     * connecting to secure Kafka.
     * 
     * @param broker
     *            {String} A string representing a list of brokers the producer
     *            can contact.
     * @param apiKey
     *            {String} The API key of the Bluemix Message Hub service.
     * @param isProducer
     *            {Boolean} Flag used to determine whether or not the
     *            configuration is for a producer.
     * @return {Properties} A properties object which stores the client
     *         configuration info.
     */
    public static final Properties getClientConfiguration(String broker,
            String apiKey, boolean isProducer) {
        Properties props = new Properties();
        InputStream propsStream;
        String fileName;

        if (isProducer) {
            fileName = "producer.properties";
        } else {
            fileName = "consumer.properties";
        }

        try {
            propsStream = new FileInputStream(System.getProperty("user.dir")
                    + File.separator + "resources" + File.separator + fileName);
            props.load(propsStream);
        } catch (IOException e) {
            System.err.println("Could not load properties from file");
            return props;
        }

        props.put("bootstrap.servers", broker);

        return props;
    }
}
