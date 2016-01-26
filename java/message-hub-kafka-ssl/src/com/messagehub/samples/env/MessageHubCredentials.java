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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MessageHubCredentials {

    private String apiKey, kafkaRestUrl, user, password;
    private String[] kafkaBrokersSasl;

    @JsonProperty("api_key")
    public String getApiKey() {
        return apiKey;
    }

    @JsonProperty("api_key")
    public void setLabel(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @JsonProperty("kafka_rest_url")
    public String getKafkaRestUrl() {
        return kafkaRestUrl;
    }

    @JsonProperty("kafka_rest_url")
    public void setKafkaRestUrl(String kafkaRestUrl) {
        this.kafkaRestUrl = kafkaRestUrl;
    }
    
    @JsonProperty
    public String getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(String user) {
        this.user = user;
    }
    
    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
    
    @JsonProperty("kafka_brokers_sasl")
    public String[] getKafkaBrokersSasl() {
        return kafkaBrokersSasl;
    }

    @JsonProperty("kafka_brokers_sasl")
    public void setKafkaBrokersSasl(String[] kafkaBrokersSasl) {
        this.kafkaBrokersSasl = kafkaBrokersSasl;
    }
}
