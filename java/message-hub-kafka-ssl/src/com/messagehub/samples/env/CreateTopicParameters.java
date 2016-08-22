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
package com.messagehub.samples.env;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateTopicParameters {

    @JsonProperty("name")
    private final String topicName;

    @JsonProperty("partitions")
    private final int partitionCount;
    
    @JsonProperty("configs")
    private final CreateTopicConfig config;

    @JsonCreator
    public CreateTopicParameters(String topicName, int partitionCount, CreateTopicConfig config) {
        this.topicName = topicName;
        this.partitionCount = partitionCount;
        this.config = config;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public CreateTopicConfig getConfig() {
		return config;
	}

	/**
     * Convert an instance of this class to its JSON
     * string representation.
     */
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            return "";
        }
    }

}
