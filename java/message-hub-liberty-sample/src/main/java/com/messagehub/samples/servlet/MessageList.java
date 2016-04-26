/**
 * Copyright 2016 IBM
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
 * (c) Copyright IBM Corp. 2016
 */
package com.messagehub.samples.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class MessageList {
    private ArrayList<String> messages;

    MessageList(String messages[]) {
        this.messages = new ArrayList<String>();

        if (messages != null && messages.length > 0) {
            for (int i = 0; i < messages.length; i++) {
                push(messages[i]);
            }
        }
    }

    MessageList(ArrayList<String> messages) {
        this.messages = new ArrayList<String>();

        if (messages != null && messages.size() > 0) {
            for (int i = 0; i < messages.size(); i++) {
                push(messages.get(i));
            }
        }
    }

    MessageList() {
        this.messages = new ArrayList<String>();
    }

    public void push(String message) {
        this.messages.add(message);
    }

    /**
     * Build message list dependent on the format Message Hub requires. The
     * message list is in the form: [{ "value": base_64_string }, ...]
     *
     * @return {String} String representation of a JSON object.
     * @throws IOException
     */
    public String build() throws IOException {
        final JsonFactory jsonFactory = new JsonFactory();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonGenerator jsonGenerator = null;

        jsonGenerator = jsonFactory.createGenerator(outputStream);

        jsonGenerator.writeStartArray();

        // Write each message as a JSON object in
        // the form:
        // { "value": base_64_string }
        for (int i = 0; i < this.messages.size(); i++) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("value");
            jsonGenerator.writeObject(this.messages.get(i));
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();

        // Close underlying streams and return string representation.
        jsonGenerator.close();
        outputStream.close();

        return new String(outputStream.toByteArray());
    }
}
