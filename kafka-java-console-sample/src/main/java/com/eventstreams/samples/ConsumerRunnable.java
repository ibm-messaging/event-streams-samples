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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);

    private final KafkaConsumer<String, String> kafkaConsumer;
    private volatile boolean closing = false;

    public ConsumerRunnable(Map<String, Object> consumerConfigs, String topic) {
        // Create a Kafka consumer with the provided client configuration
        kafkaConsumer = new KafkaConsumer<>(consumerConfigs);

        // Checking for topic existence before subscribing
        List<PartitionInfo> partitions = kafkaConsumer.partitionsFor(topic);
        if (partitions == null || partitions.isEmpty()) {
            logger.error("Topic '{}' does not exists - application will terminate", topic);
            kafkaConsumer.close(Duration.ofSeconds(5L));
            throw new IllegalStateException("Topic '" + topic + "' does not exists - application will terminate");
        } else {
            logger.info(partitions.toString());
        }

        kafkaConsumer.subscribe(Arrays.asList(topic));
    }

    @Override
    public void run() {
        logger.info("{} is starting.", ConsumerRunnable.class);

        try {
            while (!closing) {
                try {
                    // Poll on the Kafka consumer, waiting up to 3 secs if there's nothing to consume.
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(3000L));
                    
                    if (records.isEmpty()) {
                        logger.info("No messages consumed");
                    } else {
                        // Iterate through all the messages received and print their content
                        for (ConsumerRecord<String, String> record : records) {
                            logger.info("Message consumed: {}", record);
                        }
                    }

                } catch (final WakeupException e) {
                    logger.warn("Consumer closing - caught exception: {}", e, e);
                } catch (final KafkaException e) {
                    logger.error("Sleeping for 5s - Consumer has caught: {}", e, e);
                    try {
                        Thread.sleep(5000); // Longer sleep before retrying
                    } catch (InterruptedException e1) {
                        logger.warn("Consumer closing - caught exception: {}", e, e);
                    }
                }
            }
        } finally {
            kafkaConsumer.close(Duration.ofSeconds(5L));
            logger.info("{} has shut down.", ConsumerRunnable.class);
        }
    }

    public void shutdown() {
        closing = true;
        kafkaConsumer.wakeup();
        logger.info("{} is shutting down.", ConsumerRunnable.class);
    }
}
