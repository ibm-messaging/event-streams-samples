/**
 * Copyright 2018 IBM
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
 * (c) Copyright IBM Corp. 2018
 */
package com.eventstreams.samples.env;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;

public class Environment {

    private static final Logger logger = Logger.getLogger(Environment.class);

    public static EventStreamsCredentials getEventStreamsCredentials() {
        String vcapServices = System.getenv("VCAP_SERVICES");
        logger.log(Level.INFO, "VCAP_SERVICES: \n" + vcapServices);
        if (vcapServices == null) {
            logger.log(Level.ERROR, "VCAP_SERVICES environment variable is null.");
            throw new IllegalStateException("VCAP_SERVICES environment variable is null.");
        }
        return transformVcapServices(vcapServices);
    }

    private static EventStreamsCredentials transformVcapServices(String vcapServices) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode instanceCredentials = mapper.readValue(vcapServices, JsonNode.class);
            // when running in CloudFoundry VCAP_SERVICES is wrapped into a bigger JSON object
            // so it needs to be extracted. We attempt to read the "instance_id" field to identify
            // if it has been wrapped
            if (instanceCredentials.get("instance_id") == null) {
                Iterator<String> it = instanceCredentials.fieldNames();
                // Find the Event Streams service bound to this application.
                while (it.hasNext()) {
                    String potentialKey = it.next();
                    String messageHubJsonKey = "messagehub";
                    if (potentialKey.startsWith(messageHubJsonKey)) {
                        logger.log(Level.WARN, "Using the '" + potentialKey + "' key from VCAP_SERVICES.");
                        instanceCredentials = instanceCredentials.get(potentialKey)
                                .get(0)
                                .get("credentials");
                        break;
                    }
                }
            }
            return mapper.readValue(instanceCredentials.toString(), EventStreamsCredentials.class);
        } catch (IOException e) {
            logger.log(Level.ERROR, "VCAP_SERVICES environment variable parses failed.");
            throw new IllegalStateException("VCAP_SERVICES environment variable parses failed.", e);
        }
    }
}
