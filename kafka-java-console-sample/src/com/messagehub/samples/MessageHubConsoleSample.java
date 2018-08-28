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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.messagehub.samples.env.Environment;
import com.messagehub.samples.env.MessageHubCredentials;
import com.messagehub.samples.rest.RESTAdmin;

/**
 * Console-based sample interacting with Message Hub, authenticating with SASL/PLAIN over an SSL connection.
 *
 * @author IBM
 */
public class MessageHubConsoleSample {

    private static final String APP_NAME = "kafka-java-console-sample-2.0";
    private static final String DEFAULT_TOPIC_NAME = "kafka-java-console-sample-topic";
    private static final String ARG_CONSUMER = "-consumer";
    private static final String ARG_PRODUCER_ = "-producer";
    private static final String ARG_TOPIC = "-topic";
    private static final Logger logger = Logger.getLogger(MessageHubConsoleSample.class);

    private static Thread consumerThread = null;
    private static ConsumerRunnable consumerRunnable = null;
    private static Thread producerThread = null;
    private static ProducerRunnable producerRunnable = null;

    //add shutdown hooks (intercept CTRL-C etc.)
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.log(Level.WARN, "Shutdown received.");
                shutdown();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.log(Level.ERROR, "Uncaught Exception on " + t.getName() + " : " + e, e);
                shutdown();
            }
        });
    }

    private static void printUsage() {
        System.out.println("\n"
                + "Usage:\n"
                + "    java -jar build/libs/" + APP_NAME + ".jar \\\n"
                + "              <kafka_brokers_sasl> <kafka_admin_url> { <api_key> | <user = token>:<password> } [" + ARG_CONSUMER + "] \\\n"
                + "              [" + ARG_PRODUCER_ + "] [" + ARG_TOPIC + "]\n"
                + "Where:\n"
                + "    kafka_broker_sasl\n"
                + "        Required. Comma separated list of broker endpoints to connect to, for\n"
                + "        example \"host1:port1,host2:port2\".\n"
                + "    kafka_admin_url\n"
                + "        Required. The URL of the Message Hub Kafka administration REST endpoint.\n"
                + "    api_key or user/password\n"
                + "        Required. A Message Hub API key or user/password used to authenticate access to Kafka.\n"
                + "        Use user/password if the user is defined as \"token\"\n"
                + "    " + ARG_CONSUMER + "\n"
                + "        Optional. Only consume message (do not produce messages to the topic).\n"
                + "        If omitted this sample will both produce and consume messages.\n"
                + "    " + ARG_PRODUCER_ + "\n"
                + "        Optional. Only produce messages (do not consume messages from the\n"
                + "        topic). If omitted this sample will both produce and consume messages.\n"
                + "    " + ARG_TOPIC + "\n"
                + "        Optional. Specifies the Kafka topic name to use. If omitted the\n"
                + "        default used is '" + DEFAULT_TOPIC_NAME + "'\n");
    }

    public static void main(String args[])  {
        try {
            String bootstrapServers = null;
            String adminRestURL = null;
            String apiKey = null;
            boolean runConsumer = true;
            boolean runProducer = true;
            String topicName = DEFAULT_TOPIC_NAME;
            if (args.length == 0 && System.getenv("VCAP_SERVICES") == null) {
                printUsage();
                System.exit(-1);
            }
            // Check environment: VCAP_SERVICES vs command line arguments, to obtain configuration parameters
            if (args.length == 0) {

                logger.log(Level.INFO, "Using VCAP_SERVICES to find credentials.");

                MessageHubCredentials credentials = Environment.getMessageHubCredentials();

                bootstrapServers = stringArrayToCSV(credentials.getKafkaBrokersSasl());
                adminRestURL = credentials.getKafkaAdminUrl();
                apiKey = credentials.getApiKey();

            } else {
                // If running locally, parse the command line
                if (args.length < 3) {
                    logger.log(Level.ERROR, "It appears the application is running without VCAP_SERVICES but the arguments are incorrect for local mode.");
                    printUsage();
                    System.exit(-1);
                }

                logger.log(Level.INFO, "Using command line arguments to find credentials.");

                bootstrapServers = args[0];
                adminRestURL = args[1];
                apiKey = args[2];
                if (apiKey.contains(":")) {
                    String[] credentials = apiKey.split(":");
                    apiKey = credentials[1];
                } else {
                    apiKey = args[2];
                }
                if (args.length > 3) {
                    try {
                        final ArgumentParser argParser = ArgumentParser.builder()
                                .flag(ARG_CONSUMER)
                                .flag(ARG_PRODUCER_)
                                .option(ARG_TOPIC)
                                .build();
                        final Map<String, String> parsedArgs =
                                argParser.parseArguments(Arrays.copyOfRange(args, 3, args.length));
                        if (parsedArgs.containsKey(ARG_CONSUMER) && !parsedArgs.containsKey(ARG_PRODUCER_)) {
                            runProducer = false;
                        }
                        if (parsedArgs.containsKey(ARG_PRODUCER_) && !parsedArgs.containsKey(ARG_CONSUMER)) {
                            runConsumer = false;
                        }
                        if (parsedArgs.containsKey(ARG_TOPIC)) {
                            topicName = parsedArgs.get(ARG_TOPIC);
                        }
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.ERROR, e.getMessage());
                        System.exit(-1);
                    }
                }
            }

            logger.log(Level.INFO, "Kafka Endpoints: " + bootstrapServers);
            logger.log(Level.INFO, "Admin REST Endpoint: " + adminRestURL);

            //Using Message Hub Admin REST API to create and list topics
            //If the topic already exists, creation will be a no-op
            try {
                logger.log(Level.INFO, "Creating the topic " + topicName);
                String restResponse = RESTAdmin.createTopic(adminRestURL, apiKey, topicName);
                logger.log(Level.INFO, "Admin REST response :" +restResponse);

                String topics = RESTAdmin.listTopics(adminRestURL, apiKey);
                logger.log(Level.INFO, "Admin REST Listing Topics: " + topics);
            } catch (Exception e) {
                logger.log(Level.ERROR, "Error occurred accessing the Admin REST API " + e, e);
                //The application will carry on regardless of Admin REST errors, as the topic may already exist
            }

            //create the Kafka clients
            if (runConsumer) {
                Properties consumerProperties = getConsumerConfigs(bootstrapServers, apiKey);
                consumerRunnable = new ConsumerRunnable(consumerProperties, topicName);
                consumerThread = new Thread(consumerRunnable, "Consumer Thread");
                consumerThread.start();
            }

            if (runProducer) {
                Properties producerProperties = getProducerConfigs(bootstrapServers, apiKey);
                producerRunnable = new ProducerRunnable(producerProperties, topicName);
                producerThread = new Thread(producerRunnable, "Producer Thread");
                producerThread.start();
            }

            logger.log(Level.INFO, "MessageHubConsoleSample will run until interrupted.");
        } catch (Exception e) {
            logger.log(Level.ERROR, "Exception occurred, application will terminate", e);
            System.exit(-1);
        }
    }

    /*
     * convenience method for cleanup on shutdown
     */
    private static void shutdown() {
        if (producerRunnable != null)
            producerRunnable.shutdown();
        if (consumerRunnable != null)
            consumerRunnable.shutdown();
        if (producerThread != null)
            producerThread.interrupt();
        if (consumerThread != null)
            consumerThread.interrupt();
    }

    /*
     * Return a CSV-String from a String array
     */
    private static String stringArrayToCSV(String[] sArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sArray.length; i++) {
            sb.append(sArray[i]);
            if (i < sArray.length -1) sb.append(",");
        }
        return sb.toString();
    }

    static final Properties getProducerConfigs(String boostrapServers, String apikey) {
        Properties configs = new Properties();
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configs.put(ProducerConfig.CLIENT_ID_CONFIG, "kafka-java-console-sample-producer");
        configs.put(ProducerConfig.ACKS_CONFIG, "-1");
        configs.putAll(getCommonConfigs(boostrapServers, apikey));
        return configs;
    }

    static final Properties getConsumerConfigs(String boostrapServers, String apikey) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configs.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafka-java-console-sample-consumer");
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-java-console-sample-group");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        configs.putAll(getCommonConfigs(boostrapServers, apikey));
        return configs;
    }

    static final Properties getCommonConfigs(String boostrapServers, String apikey) {
        Properties configs = new Properties();
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configs.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\"" + apikey + "\";");
        configs.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
        configs.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
        configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
        return configs;
    }

}
