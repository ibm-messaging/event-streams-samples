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
 * © Copyright IBM Corp. 2015
*/
package com.example;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

public class ConsumerRunnable implements Runnable
{
	private KafkaConsumer<byte[], byte[]> kafkaConsumer;
	private ArrayList<String> topicList;
	private boolean closing;
	private int receivedMessages;

	ConsumerRunnable(String broker, String apiKey, String topic)
	{
		closing = false;
		receivedMessages = 0;

		topicList = new ArrayList<String>();

		// Provide configuration and deserialisers
		// for the key and value fields received.
		kafkaConsumer =  new KafkaConsumer<byte[], byte[]>(
				KafkaNativeSample.getClientConfiguration(broker, apiKey, false),
				new ByteArrayDeserializer(),
				new ByteArrayDeserializer());

		topicList.add(topic);
		kafkaConsumer.subscribe(topicList);
	}

	@Override
	public void run()
	{
		System.out.println(ConsumerRunnable.class.toString() + " is starting.");

		while(!closing)
		{
			try
	        {
				// Poll on the Kafka consumer every second.
	            Iterator<ConsumerRecord<byte[], byte[]>> it = kafkaConsumer.poll(1000).iterator();

	            // Iterate through all the messages received and print their content.
	            // After a predefined number of messages has been received, the client
	            // will exit.
	            while (it.hasNext())
	            {
	                ConsumerRecord<byte[], byte[]> record = it.next();
	                final String message = new String(record.value(), Charset.forName("UTF-8"));

	                System.out.println("Message: " + message);

	                if(++receivedMessages >= KafkaNativeSample.MAX_PASSED_MESSAGES)
	                {
	                	shutdown();
	                }
	            }
	            
	            kafkaConsumer.commitSync();
	            
	            Thread.sleep(1000);
	        }
			catch (final InterruptedException e)
			{
                System.err.println("Producer/Consumer loop has been unexpectedly interrupted");
                shutdown();
            }
			catch (final Exception e)
			{
	            System.err.println("Consumer has failed with exception: "+ e);
	            shutdown();
	        }
		}

		System.out.println(ConsumerRunnable.class.toString() + " is shutting down.");
		kafkaConsumer.close();
	}

	public void shutdown()
	{
		closing = true;
	}
}
