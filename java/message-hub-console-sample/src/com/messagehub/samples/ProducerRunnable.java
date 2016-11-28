/**
 * Copyright 2015-2016 IBM
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
 * (c) Copyright IBM Corp. 2015-2016
 */
package com.messagehub.samples;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class ProducerRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(ProducerRunnable.class);

    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;
    private volatile boolean closing = false;

    public ProducerRunnable(Properties producerProperties, String topic) {
        this.topic = topic;

        // Create a Kafka producer with the provided client configuration
        kafkaProducer = new KafkaProducer<String, String>(producerProperties);
        
        try {
            // Checking for topic existence.
            // If the topic does not exist, the kafkaProducer will retry for about 60 secs
            // before throwing a TimeoutException
            // see configuration parameter 'metadata.fetch.timeout.ms'
            List<PartitionInfo> partitions = kafkaProducer.partitionsFor(topic);
            logger.log(Level.INFO, partitions.toString());
        } catch (TimeoutException kte) {
            logger.log(Level.ERROR, "Topic '" + topic + "' may not exist - application will terminate");
            kafkaProducer.close();
            throw new IllegalStateException("Topic '" + topic + "' may not exist - application will terminate", kte);
        }
    }

    @Override
    public void run() {
        // Simple counter for messages sent
        int producedMessages = 0;
        logger.log(Level.INFO, ProducerRunnable.class.toString() + " is starting.");

        try {
            while (!closing) {
                String key = "key";
                String message = "This is a test message #" + producedMessages;

                try {
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                            topic,key,message);
                    
                    // Send record asynchronously
                    Future<RecordMetadata> future = kafkaProducer.send(record);
                    
                    // Synchronously wait for a response from Message Hub / Kafka.
                    // see configuration parameter 'acks'
                    RecordMetadata recordMetadata = future.get(5000, TimeUnit.MILLISECONDS);
                    producedMessages++;

                    logger.log(Level.INFO, "Message produced, offset: " + recordMetadata.offset());

                    // Short sleep for flow control in this sample app
                    Thread.sleep(2000); 

                } catch (final InterruptedException e) {
                    logger.log(Level.WARN, "Producer closing - caught exception: " + e);
                } catch (final Exception e) {
                    logger.log(Level.ERROR, "Sleeping for 5s - Producer has caught : " + e, e);
                    try {
                        Thread.sleep(5000); // Longer sleep before retrying
                    } catch (InterruptedException e1) {
                        logger.log(Level.WARN, "Producer closing - caught exception: " + e);
                    }
                }
            }
        } finally {
            kafkaProducer.close(5000, TimeUnit.MILLISECONDS);
            logger.log(Level.INFO, ProducerRunnable.class.toString() + " has shut down.");
        }
    }

    public void shutdown() {
        closing = true;
        logger.log(Level.INFO, ProducerRunnable.class.toString() + " is shutting down.");
    }
}
