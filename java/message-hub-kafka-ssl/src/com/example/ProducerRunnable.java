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
 * � Copyright IBM Corp. 2015
 */
package com.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerRunnable implements Runnable {
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
				KafkaNativeSample.getClientConfiguration(broker, apiKey, true));
	}

	@Override
	public void run() {
		System.out.println(ProducerRunnable.class.toString() + " is starting.");

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
						topic, fieldName.getBytes("UTF-8"), list.build()
								.getBytes("UTF-8"));

				// Synchronously wait for a response from Message Hub / Kafka.
				RecordMetadata m = kafkaProducer.send(record).get();

				System.out.println("Message produced, offset: " + m.offset());

				// After a predefined number of messages are received, shut
				// down.
				// This will also exit the produce loop and stop the thread it
				// is running on.
				if (++producedMessages >= KafkaNativeSample.MAX_PASSED_MESSAGES) {
					shutdown();
				}

				Thread.sleep(1000);
			} catch (final Exception e) {
				e.printStackTrace();
				shutdown();
				// Consumer will hang forever, so exit program.
				System.exit(-1);
			}
		}

		System.out.println(ProducerRunnable.class.toString()
				+ " is shutting down.");
	}

	public void shutdown() {
		closing = true;
	}
}
