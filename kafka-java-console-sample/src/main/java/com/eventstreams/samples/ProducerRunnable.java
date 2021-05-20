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
package com.eventstreams.samples;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProducerRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ProducerRunnable.class);

    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;
    private volatile boolean closing = false;

    public ProducerRunnable(Map<String, Object> producerConfigs, String topic) {
        this.topic = topic;

        // Create a Kafka producer with the provided client configuration
        kafkaProducer = new KafkaProducer<>(producerConfigs);

        try {
            // Checking for topic existence.
            // If the topic does not exist, the kafkaProducer will retry for about 60 secs
            // before throwing a TimeoutException
            // see configuration parameter 'metadata.fetch.timeout.ms'
            List<PartitionInfo> partitions = kafkaProducer.partitionsFor(topic);
            logger.info(partitions.toString());
        } catch (TimeoutException kte) {
            logger.error("Topic '{}' may not exist - application will terminate", topic);
            kafkaProducer.close(Duration.ofSeconds(5L));
            throw new IllegalStateException("Topic '" + topic + "' may not exist - application will terminate", kte);
        }
    }

    @Override
    public void run() {
        // Simple counter for messages sent
        int producedMessages = 0;
        logger.info("{} is starting.", ProducerRunnable.class);

        try {
            while (!closing) {
                String key = "key";
                String message = "{\"message\":\"This is a test message #\",\"message_number\":" + producedMessages + "}";

                try {
                    // If a partition is not specified, the client will use the default partitioner to choose one.
                    ProducerRecord<String, String> record = new ProducerRecord<>(topic,key,message);

                    // Send record asynchronously
                    Future<RecordMetadata> future = kafkaProducer.send(record);

                    // Synchronously wait for a response from Event Streams / Kafka on every message produced.
                    // For high throughput the future should be handled asynchronously.
                    RecordMetadata recordMetadata = future.get(5000, TimeUnit.MILLISECONDS);
                    producedMessages++;

                    logger.info("Message produced, offset: {}", recordMetadata.offset());

                    // Short sleep for flow control in this sample app
                    // to make the output easily understandable
                    Thread.sleep(2000L); 

                } catch (final InterruptedException e) {
                    logger.warn("Producer closing - caught exception: {}", e, e);
                } catch (final Exception e) {
                    logger.error("Sleeping for 5s - Producer has caught : {}", e, e);
                    try {
                        Thread.sleep(5000L); // Longer sleep before retrying
                    } catch (InterruptedException e1) {
                        logger.warn("Producer closing - caught exception: {}", e, e);
                    }
                }
            }
        } finally {
            kafkaProducer.close(Duration.ofSeconds(5L));
            logger.info("{} has shut down.", ProducerRunnable.class);
        }
    }

    public void shutdown() {
        closing = true;
        logger.info("{} is shutting down.", ProducerRunnable.class);
    }
}
