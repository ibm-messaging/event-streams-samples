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

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageList {
    
    private class Message {
        @JsonProperty("value")
        private String message;
        
        @JsonProperty("timestamp")
        private String commitTime;
        
        public Message(String message) {
            this.message = message;
            this.commitTime = new Date().toString();
        }
    }
    
    private ArrayList<Message> messages;

    /**
     * Constructs an instance of MessageList with the
     * provided array of strings. If the array is null, the message
     * list is only initialized.
     * 
     * @param messages {String[]} Array of strings to add to the message list.
     */
    public MessageList(String messages[]) {
        this.messages = new ArrayList<Message>();

        if (messages != null && messages.length > 0) {
            for (int i = 0; i < messages.length; i++) {
                push(messages[i]);
            }
        }
    }

    /**
     * Constructs an instance of MessageList with the
     * provided ArrayList of strings. If the ArrayList is null, the message
     * list is only initialized.
     * 
     * @param messages {String[]} Array of strings to add to the message list.
     */
    public MessageList(ArrayList<String> messages) {
        this.messages = new ArrayList<Message>();

        if (messages != null && messages.size() > 0) {
            for(String message : messages) {
                push(message);
            }
        }
    }

    /**
     * Constructs an empty instance of MessageList.
     */
    public MessageList() {
        this.messages = new ArrayList<Message>();
    }

    /**
     * Adds a new message to the message list.
     * @param message {String} The message to add to the list.
     */
    public void push(String message) {
        this.messages.add(new Message(message));
    }

    /**
     * Build message list dependent on the format Message Hub requires. The
     * message list is in the form: [{ "value": string, "time": timestamp }, ...]
     * 
     * @return {String} String representation of a JSON object.
     */
    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(messages);
        } catch(final JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
