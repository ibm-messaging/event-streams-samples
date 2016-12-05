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
package com.messagehub.samples.rest;

/**
 * Facade to access IBM Message Hub REST Admin API
 * 
 * @author IBM
 */
public class RESTAdmin {
    
    //used as topic retention period
    private static final long _24H_IN_MILLISECONDS = 3600000L*24;

    /**
     * Creates a topic or ignores an 'Already Exists' response
     * <p/>
     * @param restURL HTTPS endpoint URL
     * @param apiKey Message Hub API Key 
     * @param topicName Name of the topic
     * @return the body of the HTTP response
     * @throws Exception if an unexpected error occurs
     */
    public static String createTopic(String restURL, String apiKey, String topicName) throws Exception {
        RESTRequest restApi = new RESTRequest(restURL, apiKey);

        // Create a topic, ignore a 422 response - this means that the
        // topic name already exists.
        return restApi.post("/admin/topics",
                new CreateTopicParameters(topicName, 
                        1 /* one partition */, 
                        new CreateTopicConfig(_24H_IN_MILLISECONDS)).toString(),
                new int[] { 422 });
    }

    /**
     * Returns all the topics available to the user in a single string
     * <p/>
     * @param restURL HTTPS endpoint URL
     * @param apiKey Message Hub API Key 
     * @return all the topics available to the user in a single string
     * @throws Exception if an unexpected error occurs
     */
    public static String listTopics(String restURL, String apiKey) throws Exception {
        RESTRequest restApi = new RESTRequest(restURL, apiKey);
        return restApi.get("/admin/topics", false);   
    }

}
