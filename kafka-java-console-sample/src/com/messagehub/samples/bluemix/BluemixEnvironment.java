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
package com.messagehub.samples.bluemix;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Facade class with Bluemix utily methods
 * 
 * @author IBM
 *
 */
public class BluemixEnvironment {
    
    private static final Logger logger = Logger.getLogger(BluemixEnvironment.class);

    /**
     * Check whether the code is executing in a Bluemix Java Buildpack
     */
    public static boolean isRunningInBluemix() {
        String userDir = System.getProperty("user.dir");
        File buildpack = new File(userDir + File.separator + ".java-buildpack");
        String vcapServices = System.getenv("VCAP_SERVICES");
        
        if (buildpack.exists() && (vcapServices == null)) {
            throw new IllegalStateException("ASSERTION FAILED: buildpack.exists() but VCAP_SERVICES==null");
        }
        
        return buildpack.exists();
    }

    /**
     * Parses VCAP_SERVICES to extract Message Hub connection configuration
     * @return an instance of MessageHubCredentials
     * @throws IOException on parsing error
     * @throws IllegalStateException if there is no Message Hub service bound to the application
     */
    public static MessageHubCredentials getMessageHubCredentials() throws IOException  {
        // Arguments parsed via VCAP_SERVICES environment variable.
        String vcapServices = System.getenv("VCAP_SERVICES");
        ObjectMapper mapper = new ObjectMapper();

            try {
                // Parse VCAP_SERVICES into Jackson JsonNode, then map the 'messagehub' entry
                // to an instance of MessageHubEnvironment.
                JsonNode vcapServicesJson = mapper.readValue(vcapServices, JsonNode.class);
                ObjectMapper envMapper = new ObjectMapper();
                String vcapKey = null;
                Iterator<String> it = vcapServicesJson.fieldNames();

                // Find the Message Hub service bound to this application.
                while (it.hasNext() && vcapKey == null) {
                    String potentialKey = it.next();

                    if (potentialKey.startsWith("messagehub")) {
                        logger.log(Level.INFO, "Using the '" + potentialKey + "' key from VCAP_SERVICES.");
                        vcapKey = potentialKey;
                    }
                }

                // Sanity assertion check
                if (vcapKey == null) {
                    logger.log(Level.ERROR,
                            "Error while parsing VCAP_SERVICES: A Message Hub service instance is not bound to this application.");
                    throw new IllegalStateException("Error while parsing VCAP_SERVICES: A Message Hub service instance is not bound to this application.");
                }

                MessageHubEnvironment messageHubEnvironment = envMapper.readValue(vcapServicesJson.get(vcapKey).get(0).toString(), MessageHubEnvironment.class);
                MessageHubCredentials credentials = messageHubEnvironment.getCredentials();
                return credentials;
            } catch (IOException e) {
                logger.log(Level.ERROR, "Failed parsing or processing VCAP_SERVICES", e);
                throw e;
            }
    }

}
