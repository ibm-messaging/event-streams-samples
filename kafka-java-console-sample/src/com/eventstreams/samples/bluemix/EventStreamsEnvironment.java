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
package com.eventstreams.samples.bluemix;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EventStreamsEnvironment {

    private String name, label, plan;
    private EventStreamsCredentials credentials;
    
    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty
    public String getLabel() {
        return label;
    }

    @JsonProperty
    public void setLabel(String label) {
        this.label = label;
    }
    
    @JsonProperty
    public String getPlan() {
        return plan;
    }

    @JsonProperty
    public void setPlan(String plan) {
        this.plan = plan;
    }
    
    @JsonProperty
    public EventStreamsCredentials getCredentials() {
        return credentials;
    }

    @JsonProperty
    public void setCredentials(EventStreamsCredentials credentials) {
        this.credentials = credentials;
    }
}
