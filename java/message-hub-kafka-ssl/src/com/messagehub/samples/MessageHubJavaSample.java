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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagehub.samples.env.MessageHubCredentials;
import com.messagehub.samples.env.MessageHubEnvironment;

/**
 * Sample used for interacting with Message Hub over Secure Kafka / Kafka Native
 * channels.
 * 
 * @author IBM
 */
public class MessageHubJavaSample {
    
    private static final Logger logger = Logger.getLogger(MessageHubJavaSample.class);
    private static String userDir, resourceDir;
    private static boolean isDistribution;
    
    public static void main(String args[]) throws InterruptedException,
            ExecutionException, IOException {

        String topic = "mytopic";
        String kafkaHost = null;
        String restHost = null;
        String apiKey = null;
        Thread consumerThread, producerThread;
        RESTRequest restApi = null;
        
        userDir = System.getProperty("user.dir");
        
        isDistribution = new File(userDir + File.separator + ".java-buildpack").exists();

        if(isDistribution) {
            logger.log(Level.INFO, "Running in distribution mode.");
            resourceDir = userDir + File.separator + "message-hub-kafka-ssl-1.0" + File.separator + "bin" + File.separator + "resources";
        } else {
            logger.log(Level.INFO, "Running in local mode.");
            resourceDir = userDir + File.separator + "resources";
        }
        
        // Set JAAS configuration property.
        if(System.getProperty("java.security.auth.login.config") == null) {
            System.setProperty("java.security.auth.login.config", resourceDir + File.separator + "jaas.conf");
        }

        logger.log(Level.INFO, "Starting Message Hub Java Sample");
        
        if(args.length == 3) {
            // Arguments parsed from the command line.
            kafkaHost = args[0];
            restHost = args[1];
            apiKey = args[2];
        } else {
            // Arguments parsed via VCAP_SERVICES environment variable.
            String vcapServices = System.getenv("VCAP_SERVICES");
            ObjectMapper mapper = new ObjectMapper();
            
            if(vcapServices != null) {
                try {
                    // Parse VCAP_SERVICES into Jackson JsonNode, then map the 'messagehub' entry
                    // to an instance of MessageHubEnvironment.
                    JsonNode vcapServicesJson = mapper.readValue(vcapServices, JsonNode.class);
                    ObjectMapper envMapper = new ObjectMapper();
                    
                    if(vcapServicesJson.has("messagehub")) {
                        MessageHubEnvironment messageHubEnvironment = envMapper.readValue(vcapServicesJson.get("messagehub").get(0).toString(), MessageHubEnvironment.class);
                        MessageHubCredentials credentials = messageHubEnvironment.getCredentials();
                        
                        kafkaHost = credentials.getKafkaBrokersSasl()[0];
                        restHost = credentials.getKafkaRestUrl();
                        apiKey = credentials.getApiKey();
                        
                        updateJaasConfiguration(credentials);
                    } else {
                        logger.log(Level.ERROR, "Error while parsing VCAP_SERVICES: A Message Hub service instance is not bound to this application.");
                        return;
                    }
                } catch(final Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                logger.log(Level.ERROR, "VCAP_SERVICES environment variable is null, are you running outside of Bluemix? If you are, consider the following usage:\n\n" +
                        "java -Djava.security.auth.login.config=resources/jaas.conf -jar <name_of_jar>.jar <kafka_endpoint> <rest_endpoint> <api_key>");
                return;
            }
        }
        
        logger.log(Level.INFO, "Sample will run until interrupted.");
        logger.log(Level.INFO, "Resource directory: " + resourceDir);
        logger.log(Level.INFO, "Kafka Endpoint: " + kafkaHost);
        logger.log(Level.INFO, "Rest API Endpoint: " + restHost);
        
        restApi = new RESTRequest(restHost, apiKey);

        // Create a topic, ignore a 422 response - this means that the
        // topic name already exists.
        restApi.post("/admin/topics", "{ \"name\": \"" + topic + "\" }",
                new int[] { 422 });

        String topics = restApi.get("/admin/topics", false);

        logger.log(Level.INFO, "Topics: " + topics);

        producerThread = createMessageProducer(kafkaHost, apiKey, topic);
        consumerThread = createMessageConsumer(kafkaHost, apiKey, topic);

        // Start producer and consumer threads.
        if (consumerThread != null) {
            consumerThread.start();
        } else {
            logger.log(Level.ERROR, "Consumer thread is null. Make sure all provided details are valid.");
        }

        if (producerThread != null) {
            producerThread.start();
        } else {
            logger.log(Level.ERROR, "Producer thread is null. Make sure all provided details are valid.");
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
            propsStream = new FileInputStream(resourceDir + File.separator + fileName);
            props.load(propsStream);
            propsStream.close();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Could not load properties from file");
            return props;
        }

        props.put("bootstrap.servers", broker);
        
        if(isDistribution) {
            props.put("ssl.truststore.location", userDir + "/.java-buildpack/open_jdk_jre/lib/security/cacerts");
        }

        return props;
    }
    
    /**
     * Updates JAAS config file with provided credentials.
     * @param credentials {MessageHubCredentials} Object which stores Message Hub credentials
     *      retrieved from the VCAP_SERVICES environment variable.
     */
    private static void updateJaasConfiguration(MessageHubCredentials credentials) {
        String templatePath = resourceDir + File.separator + "templates" + File.separator + "jaas.conf.template";
        String path = resourceDir + File.separator + "jaas.conf";
        OutputStream jaasStream = null;
        
        logger.log(Level.INFO, "Updating JAAS configuration");
        
        try {
            String templateContents = new String(Files.readAllBytes(Paths.get(templatePath)));
            jaasStream = new FileOutputStream(path, false);
            
            // Replace username and password in template and write
            // to jaas.conf in resources directory.
            String fileContents = templateContents
                .replace("$USERNAME", credentials.getUser())
                .replace("$PASSWORD", credentials.getPassword());
            
            jaasStream.write(fileContents.getBytes(Charset.forName("UTF-8")));
        } catch (final FileNotFoundException e) {
            logger.log(Level.ERROR, "Could not load JAAS config file at: " + path);
        } catch (final IOException e) {
            logger.log(Level.ERROR, "Writing to JAAS config file:");
            e.printStackTrace();
        } finally {
            if(jaasStream != null) {
                try {
                    jaasStream.close();
                } catch(final Exception e) {
                    logger.log(Level.ERROR, "Closing JAAS config file:");
                    e.printStackTrace();
                }
            }
        }
    }
}
