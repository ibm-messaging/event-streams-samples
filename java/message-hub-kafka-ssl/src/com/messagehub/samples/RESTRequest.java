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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class RESTRequest {
    private String apiKey, baseUrl;

    public RESTRequest(String baseUrl, String apiKey) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * Execute a GET request against the specified REST target.
     *
     * @param target
     *            {String} The REST API target to run against (for example,
     *            '/admin/topics')
     * @param acceptHeader
     *            {Boolean} A flag to notify the caller whether or not to
     *            include the 'Accept' header in its request.
     * @return {String} The response received from the server.
     */
    public String get(String target, boolean acceptHeader) {
        HttpsURLConnection connection = null;

        if (!target.startsWith("/")) {
            target = "/" + target;
        }

        try {
            // Create secure connection to the REST URL.
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);

            URL url = new URL(baseUrl + target);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setRequestMethod("GET");
            // Apply API key header and kafka content type Accept header if
            // the 'acceptHeader' flag is set to true.
            connection.setRequestProperty("X-Auth-Token", this.apiKey);

            if (acceptHeader) {
                connection.setRequestProperty("Accept",
                        "application/vnd.kafka.binary.v1+json");
            }

            // Read the response data from the request and return
            // it to the function caller.
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            StringBuffer response = new StringBuffer();

            while ((inputLine = rd.readLine()) != null) {
                response.append(inputLine);
            }

            rd.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return "";
    }

    /**
     * Execute a GET request against the specified REST target.
     *
     * @param target
     *            {String} The REST API target to run against (for example,
     *            '/admin/topics')
     * @param body
     *            {String} The data to be provided in the body section of the
     *            POST request.
     * @param ignoredErrorCodes
     *            {int[]} An list of error codes which will be ignored as a
     *            side-effect of the request. Can be provided as null.
     * @return {String} The response received from the server.
     */
    public String post(String target, String body, int[] ignoredErrorCodes) {
        HttpsURLConnection connection = null;
        int responseCode = 0;

        if (!target.startsWith("/")) {
            target = "/" + target;
        }

        try {

            // Create secure connection to the REST URL.
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);

            URL url = new URL(baseUrl + target);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Apply headers, in this case, the API key and Kafka content type.
            connection.setRequestProperty("X-Auth-Token", this.apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // Send the request, writing the body data
            // to the output stream.
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(body);
            wr.close();

            responseCode = connection.getResponseCode();

            // Retrieve the response, transform it, then
            // return it to the caller.
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

            return response.toString();
        } catch (Exception e) {
            boolean isIgnored = false;

            // Filter out error codes which are ignored. If the
            // response code is in the ignore list, the error
            // is not printed.
            if (ignoredErrorCodes != null) {
                for (int i = 0; i < ignoredErrorCodes.length; i++) {
                    if (ignoredErrorCodes[i] == responseCode) {
                        isIgnored = true;
                    }
                }
            }

            if (!isIgnored) {
                e.printStackTrace();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return "";
    }
}
