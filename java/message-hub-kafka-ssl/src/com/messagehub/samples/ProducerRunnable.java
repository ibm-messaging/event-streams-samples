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
package com.messagehub.samples;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.messagehub.samples.env.MessageList;

public class ProducerRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(ProducerRunnable.class);
    private KafkaProducer<byte[], byte[]> kafkaProducer;
    private String topic;
    private boolean closing = false;
    private int producedMessages = 0;

    /**
     * Produce a message on the provided topic.
     *
     * @param broker
     *            {String} A string representing a list of brokers the producer
     *            can contact.
     * @param apiKey
     *            {String} The API key of the Bluemix Message Hub service.
     * @param topic
     *            {String} The topic to post the provided message to.
     * @param message
     *            {String} A stringified version of a JSON object, representing
     *            an array of messages to be sent.
     */
    ProducerRunnable(String broker, String apiKey, String topic) {
        this.topic = topic;
        // Create a Kafka producer, providing client configuration.
        this.kafkaProducer = new KafkaProducer<byte[], byte[]>(
                MessageHubJavaSample.getClientConfiguration(broker, apiKey, true));
    }

    @Override
    public void run() {
        logger.log(Level.INFO, ProducerRunnable.class.toString() + " is starting.");

        while (!closing) {
            String fieldName = "records";
            // Push a message into the list to be sent.
            MessageList list = new MessageList();
            list.push("This is a test message" + producedMessages);

            try {
                // Create a producer record which will be sent
                // to the Message Hub service, providing the topic
                // name, field name and message. The field name and
                // message are converted to UTF-8.
                ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(
                    topic,
                    fieldName.getBytes("UTF-8"),
                    list.toString().getBytes("UTF-8"));

                // Synchronously wait for a response from Message Hub / Kafka.
                RecordMetadata m = kafkaProducer.send(record).get();
                producedMessages++;

                logger.log(Level.INFO, "Message produced, offset: " + m.offset());

                Thread.sleep(1000);
            } catch (final Exception e) {
                e.printStackTrace();
                shutdown();
                // Consumer will hang forever, so exit program.
                System.exit(-1);
            }
        }

        logger.log(Level.INFO, ProducerRunnable.class.toString() + " is shutting down.");
    }

    public void shutdown() {
        closing = true;
    }
}
