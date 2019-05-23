/**
 * Copyright 2015-2018 IBM
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
 * © Copyright IBM Corp. 2015-2018
 */

var producer;
var exports = module.exports = {};

/**
 * Constructs a Kafka Producer and registers listeners on the most common events
 * 
 * @param {object} Kafka - an instance of the node-rdkafka module
 * @param {object} producer_opts - producer configuration
 * @param {string} topicName - name of the topic to produce to
 * @param {function} shutdown - shutdown function
 * @return {Producer} - the Kafka Producer instance
*/
exports.buildProducer = function(Kafka, producer_opts, topicName, shutdown) {
    // Create Kafka producer
    var topicOpts = {
        'request.required.acks': -1,
        'produce.offset.report': true
    };
    producer = new Kafka.Producer(producer_opts, topicOpts);
    producer.setPollInterval(100);

    // Register listener for debug information; only invoked if debug option set in driver_options
    producer.on('event.log', function(log) {
        console.log(log);
    });

    // Register error listener
    producer.on('event.error', function(err) {
        console.error('Error from producer:' + JSON.stringify(err));
    });

    // Register delivery report listener
    producer.on('delivery-report', function(err, dr) {
        if (err) {
            console.error('Delivery report: Failed sending message ' + dr.value);
            console.error(err);
            // We could retry sending the message
        } else {
            console.log('Message produced, partition: ' + dr.partition + ' offset: ' + dr.offset);
        }
    });

    function sendMessages(counter, topic, partition) {
        var message = new Buffer('This is a test message #' + counter);
        var key = 'Key' + counter;
        // Short sleep for flow control in this sample app
        // to make the output easily understandable
        var timeout = 2000;
        try {
            producer.produce(topic, partition, message, key);
            counter++;
        } catch (err) {
            console.error('Failed sending message ' + message);
            console.error(err);
            timeout = 5000; // Longer wait before retrying
        }
        setTimeout(function () {
            if (producer.isConnected()) {
                sendMessages(counter, topic, partition);
            }
        }, timeout);
    }

    // Register callback invoked when producer has connected
    producer.on('ready', function() {
        console.log('The producer has connected.');

        // request metadata for all topics
        producer.getMetadata({
            timeout: 10000
        }, 
        function(err, metadata) {
            if (err) {
                console.error('Error getting metadata: ' + JSON.stringify(err));
                shutdown(-1);
            } else {
                console.log('Producer obtained metadata: ' + JSON.stringify(metadata));
                var topicsByName = metadata.topics.filter(function(t) {
                    return t.name === topicName;
                });
                if (topicsByName.length === 0) {
                    console.error('ERROR - Topic ' + topicName + ' does not exist. Exiting');
                    shutdown(-1);
                }
            }
        });
        var counter = 0;

        // Start sending messages
        sendMessages(counter, topicName, null);
    });
    return producer;
}
