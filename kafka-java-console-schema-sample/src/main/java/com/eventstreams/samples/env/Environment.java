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
import java.io.IOException;
import java.util.Iterator;

import org.apache.kafka.common.errors.IllegalSaslStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Environment {

    private static final Logger logger = LoggerFactory.getLogger(Environment.class);
    private static final String SERVICE_NAME = "messagehub";

    public static EventStreamsCredentials getEventStreamsCredentials() {
        String vcapServices = System.getenv("VCAP_SERVICES");
        logger.info("VCAP_SERVICES: \n{}", vcapServices);
        try {
            if (vcapServices != null) {
                JsonNode mhub = parseVcapServices(vcapServices);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(mhub.toString(), EventStreamsCredentials.class);
            } else {
                logger.error("VCAP_SERVICES environment variable is null.");
                throw new IllegalStateException("VCAP_SERVICES environment variable is null.");
            }
        } catch (IOException ioe) {
            logger.error("VCAP_SERVICES environment variable parsing failed.");
            throw new IllegalStateException("VCAP_SERVICES environment variable parsing failed.", ioe);
        }
    }

    private static JsonNode parseVcapServices(String vcapServices) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode vcapServicesJson = mapper.readValue(vcapServices, JsonNode.class);

        // when running in CloudFoundry VCAP_SERVICES is wrapped into a bigger JSON object
        // so it needs to be extracted. We attempt to read the "instance_id" field to identify
        // if it has been wrapped
        if (vcapServicesJson.get("instance_id") != null) {
            return vcapServicesJson;
        } else {
            String vcapKey = null;
            Iterator<String> it = vcapServicesJson.fieldNames();
            // Find the Event Streams service bound to this application.
            while (it.hasNext() && vcapKey == null) {
                String potentialKey = it.next();
                if (potentialKey.startsWith(SERVICE_NAME)) {
                    logger.warn("Using the '{}' key from VCAP_SERVICES.", potentialKey);
                    vcapKey = potentialKey;
                }
            }

            if (vcapKey == null) {
                throw new IllegalSaslStateException("No Event Streams service bound");
            } else {
                return vcapServicesJson.get(vcapKey).get(0).get("credentials");
            }
        }
    }
}
